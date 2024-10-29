![](https://i-blog.csdnimg.cn/direct/5239ae895cf54b51b73b0f9b46d5e025.png#pic_center)

[Github 地址](https://github.com/diandian18/react-antd-console) | [文档](https://doc.react-antd-console.site) | [在线预览](https://template.react-antd-console.site) | [拓展 pro 版在线预览](https://react-antd-console.site)

![](https://i-blog.csdnimg.cn/direct/7bc4d9d75c4543f19d211beacdafb7b8.png#pic_center)

[react-antd-console](https://github.com/diandian18/react-antd-console) 是一个后台管理系统的前端解决方案，封装了后台管理系统必要功能（如登录、鉴权、菜单、面包屑等），帮助开发人员专注于业务快速开发。项目基于 `React 18`、`Ant design 5`、`Vite` 和 `TypeScript` 等新版本。对于使用到的各项技术，会被持续更新至最新版本。可放心用于生产环境。

为了方便大家更好的掌握和使用本项目，推出系列文章:

*   腿夹腿，带你用 react 撸后台，系列一（ Vite 篇）
*   腿夹腿，带你用 react 撸后台，系列二（项目骨架篇）

如果你喜欢这个项目或认为对你有用，欢迎使用体验和 [Star](https://github.com/diandian18/react-antd-console)

1\. 概述
------

上篇我们搞定了构建工具，接下来，我们继续搭建项目骨架。值得一提的是，项目骨架搭建好后，会是一个通用的 react 开发模板，也可以用来开发其他 react 项目

2\. react 初始化
-------------

上篇提到 `<root>/index.html` 引入了 `src/main.tsx`，接下来我们在 `<root>/index.html` 中添加一个 `div` 元素作为 react 根组件的容器

    <!-- <root>/index.html -->
    <html>
      <body>
        <div id="root"></div>
        <script type="module" src="/src/main.tsx"></script>
      </body>
    </html>


然后在 `src/main.tsx` 中初始化 react

    // src/main.tsx
    import { createRoot } from 'react-dom/client';
    import App from './App';
    // createRoot lets you create a root to display React components inside a browser DOM node.
    const root = createRoot(document.getElementById('root')!);
    root.render(
        <App />
      );


参考文档：

*   [React 官方文档-在页面的任何位置渲染 React 组件](https://zh-hans.react.dev/learn/add-react-to-an-existing-project#step-2-render-react-components-anywhere-on-the-page)

3\. react-router 初始化
--------------------

### 3.1 准备工作

在初始化 react-router 之前，我们先来考虑一些事情：

#### 3.1.1 一套配置生成多种数据

众所周知，一个路由对应一个页面。 而侧边菜单，往往也是一个路由对应一个菜单项（除了一些特定的页面例如登录页/403 页等，和从某列表点开来的详情/编辑页等）。 而面包屑组件，也是根据菜单数据生成。 因此，我们需要只用一套数据，就可以自动生成 react 路由、菜单、面包屑等数据。

故作如下设计:

数据

类型

来源

作用

routesConfig

`RouteConfig[]`

手动配置

配置路由

reactRoutes

[`RouteObject[]`](https://reactrouter.com/en/main/route/route#type-declaration)

自动生成

用于生成 `react-router` 路由

routes

`RouteConfig[]`

自动生成

树型结构，用于生成 **菜单**、**面包屑** 等数据

flattenRoutes

`RouteConfig[]`

自动生成

`routes` 的展平数据结构，方便查询路由

    interface RouteConfig {
        /** 配置数据: 路径，同 react-router */
        path: string;
        /** 生成数据: 对应 react-router 的 pathname */
        pathname?: string;
        /**
         * 生成数据: ['', '/layout', '/layout/layout-children1',
         *  '/layout/layout-children1/permission']
         */
        collecttedPathname?: string[];
        /** 生成数据: ['', 'layout', 'layout-children1', 'permission'] */
        collecttedPath?: string[];
        /** 配置数据: 组件的文件地址 */
        component?: () => Promise<any>;
        /** 配置数据: 隐藏在菜单 */
        hidden?: boolean;
        /** 配置数据: 菜单名称 */
        name?: string;
        /** 配置数据: 菜单 icon */
        icon?: React.ReactNode;
        /** 配置数据: 页面标题，不传则用 name */
        helmet?: string;
        /** 配置数据: 菜单权限 */
        permission?: string;
        /** 配置数据: 重定向 path */
        redirect?: string;
        /** 配置数据: 将子路由的菜单层级提升到本级 */
        flatten?: boolean;
        /** 配置数据: 子路由，同 react-router */
        children?: RouteConfig[];
        /** 配置数据: 同 react-router */
        caseSensitive?: boolean;
        /** 配置数据: 是否是外链 */
        external?: boolean;
        /** 生成数据: 父路由 */
        parent?: RouteConfig;
    };


为此，我们封装了 Router 类，导出在了 [react-router-toolset](https://github.com/diandian18/react-router-toolset) 中。 react-router-toolset 还导出了 useRouter 方法，用于在 react 组件中获取最新的上述数据。

用法：

    // router/index.ts
    import { Router } from 'react-router-toolset';
    import { routesConfig } from './config';
    
    /**
     * 路由配置在 `src/router/config/index.tsx` 文件的 `routesConfig` 中
     * routesConfig 要满足 RouteConfig 类型约束
     * routesConfig 配置参考:
     * https://github.com/diandian18/react-antd-console/blob/master/src/router/config/index.tsx
     */
    const router = new Router(routesConfig);
    
    // router 包含了 reactRoutes/routes/flattenRoutes 等数据
    export default router;


#### 3.1.2 组件外跳转

react-router 只提供了组件内跳转路由的 api [useNavigate](https://reactrouter.com/en/main/hooks/use-navigate#usenavigate)。我们还希望在组件外可以跳转。而 [history](https://github.com/remix-run/history) 是专门做这个事情的 另一方面，react-router 没有提供 history 模式的 Router 组件，我们需要封装一个

    // history.ts
    import { createBrowserHistory } from 'history';
    const history = createBrowserHistory();
    export default history;
    

    // HistoryRouter.tsx
    import { useState, useLayoutEffect } from 'react';
    import { Router } from 'react-router-dom';
    import type { BrowserHistory } from 'history';
    
    export interface HistoryRouterProps {
      history: BrowserHistory
      basename?: string
      children?: React.ReactNode
    }
    
    export default function HistoryRouter({
      basename,
      children,
      history,
    }: HistoryRouterProps) {
      const [state, setState] = useState({
        action: history.action,
        location: history.location,
      });
    
      useLayoutEffect(() => history.listen(setState), [history]);
    
      return (
        <Router
          basename={basename}
          location={state.location}
          navigationType={state.action}
          navigator={history}
        >
          { children }
        </Router>
      );
    }


我们把 `history` 和 `HistoryRouter` 都导出在了 react-router-toolset 中

    // router/index.ts
    export * from 'react-router-toolset'; // 包括 history 和 HistoryRouter
    

    // 组件外跳转示例
    import { history } from '@/router';
    history.push('/login');


### 3.2 初始化

准备工作做好后，我们可以很简洁地初始化 react-router

    // src/main.tsx
    import { history, HistoryRouter } from '@/router';
    
    root.render(
      <HistoryRouter history={history}>
        <App />
      </HistoryRouter>,
    );
    

    // src/App.tsx
    import { useRoutes } from 'react-router-dom';
    import router from '@/router';
    
    function App() {
      const element = useRoutes(router.reactRoutes);
      return element;
    }


### 3.3 关于 react-router-toolset

react-router-toolset 作为一个通用的 react-router 工具集，发布了 npm 包，[源码地址](https://github.com/diandian18/react-router-toolset)

下面额外介绍一些其他有用的方法:

    /**
     * 设置路由项。setItem
     * @param pathname 指定路由
     * @param cb 参数为 pathname 对应的路由
     */
    Router.setItem: (pathname: string | ((routesConfigItem: RouteConfig) => void), cb?: (routesConfigItem: RouteConfig) => void) => void
    
    /**
     * 设置 pathname 的兄弟路由
     * @param pathname 指定路由
     * @param cb 参数 routesConfigs 为 pathname 的兄弟路由
     * @param cb 参数 parentRoute 为 pathname 的父路由
     */
    Router.setSiblings(pathname: string | ((routesConfig: RouteConfig[], parentRoute: RouteConfig) => void), cb?: (routesConfig: RouteConfig[], parentRoute: RouteConfig) => void): void
    
    /**
     * 根据 pathname 获取 router 的 path
     * router 的 path 里可能有:id
     * @example '/123/home' -> '/:id/home'
     */
    Router.getRoutePath(pathname: string): string
    
    /**
     * 根据当前路由 params
     * 替换掉目标 routePath 中的动态路由参数如":id"
     * @example '/:id/home' -> '/123/home'
     */
    (method) Router.getPathname(routePath: string): string
    
    /**
     * 在 react 组件中获取最新的 reactRoutes/routes/flattenRoutes/curRoute
     */
    function useRouter(router: Router): {
      reactRoutes: RouteObject[];
      routes: RouteConfig[];
      flattenRoutes: Map<string, RouteConfig>;
      curRoute: RouteConfig | undefined;
    }


4\. 继续完善
--------

### 4.1 动态浏览器标题

每个页面都有自己的标题，我们希望在浏览器标题上动态展示页面对应的标题，可以使用 [react-helmet-async](https://github.com/staylor/react-helmet-async) 实现

    // src/App.tsx
    import { Helmet, HelmetProvider } from 'react-helmet-async';
    import router, { useRouter } from '@/router';
    
    function App() {
      // useRouter 除了返回路由相关数据，还返回了当前路由数据
      const { curRoute } = useRouter(router);
      const element = useRoutes(router.reactRoutes);
      const { t: t_menu } = useTranslation('menu');
    
      return (
        <HelmetProvider>
          <Helmet>
            <title>{curRoute?.name ? `${t_menu(curRoute.name)} - ${DEFAULT_TITLE}` : DEFAULT_TITLE}</title>
          </Helmet>
          { element }
        </HelmetProvider>
      );
    }


### 4.2 antd

[antd](https://ant.design/index-cn) 甚至不用初始化，安装好直接用即可。

    npm i -S antd


打包的时候还能自动做 tree shaking （说是这么说，但是 antd 打出来的包依然很大。如果你嫌大，可以把 antd 做 cdn 引入。可以参考 [vite-plugin-cdn-import](https://github.com/mmf-fe/vite-plugin-cdn-import) 和 [Vite 配置 build.rollupOptions.external](https://rollupjs.org/configuration-options/#external) 去解决，在此不做展开）

值得一提的是，antd 功能覆盖的场景还是非常多的，很可能有一些很有用的功能，由于我们查看文档不够仔细而被忽略掉

[antd 文档](https://ant.design/index-cn)还是比较详细的，平时开发会经常用到，随时查阅，在此不做赘述

### 4.3 请求

可参考 [react-antd-console 文档](https://doc.react-antd-console.site/development/request.html)

### 4.4 Mock

可参考 [react-antd-console 文档](https://doc.react-antd-console.site/development/mock.html)

### 4.5 国际化

可参考 [react-antd-console 文档](https://doc.react-antd-console.site/development/i18n.html)

5\. 目录结构
--------

完成上述工作后，我们最后再确定下目录结构

    ./src
    ├── assets      # 公共静态资源
    ├── components  # 公共组件
    ├── consts      # 公共常量
    ├── hooks       # 公共 hooks
    ├── http        # http
    ├── layouts     # 通用布局
    ├── locales     # 多语言配置
    ├── mock        # mock
    ├── models      # 公共数据管理
    ├── pages       # 页面组件
    ├── router      # 路由配置
    ├── services    # 接口配置
    ├── styles      # 公共样式
    ├── utils       # 公共工具
    ├── App.tsx     # 根组件
    └── main.tsx    # 入口组件


下面从:

*   固定目录(不需要修改)
*   配置目录
*   公共目录

三种目录概述

### 5.1 固定目录

*   `src/lyaouts`: 通用布局。以后介绍
*   `src/http`: 封装了 `axios`，配置了请求和响应拦截，导出了 `axios` 实例
*   `src/mock`: 封装了 `msw`，若要添加 mock 文件，请在 `src/mock/browser.ts` 中添加

### 5.2 配置目录

*   `src/router`: 包含路由配置和导出了一些重要的路由方法
*   `src/pages`: 业务页面
*   `src/locales`: 多语言配置
*   `src/services`: 接口配置

### 5.3 公共目录

以下目录，定义为通用目录，存放的都是可以复用的代码

*   `src/assets`: 公共静态资源
*   `src/components`: 公共组件
*   `src/consts`: 公共常量
*   `src/hooks`: 公共 hooks
*   `src/models`: 公共数据管理。以后介绍
*   `src/styles`: 公共样式
*   `src/utils`: 公共工具

> 逻辑相关性强的文件之间，应当尽可能的靠近，最好放在同一目录下。而公共目录，应当只包含：**全局**或**可复用**的代码

> 特别需要注意的是，除非由特殊原因，我们不认为每个**页面**组件都需要对应一个**model**数据，因为这么做会增加结构复杂度和维护难度。**model**中的数据，应该是一些**全局**或**可复用**的数据

至此，项目骨架搭建完毕。

6\. 系列文章
--------

*   腿夹腿，带你用 react 撸后台，系列一（ Vite 篇）
*   腿夹腿，带你用 react 撸后台，系列二（项目骨架篇）

如果你喜欢这个项目或认为对你有用，欢迎使用体验和 [Star](https://github.com/diandian18/react-antd-console)

[Github 地址](https://github.com/diandian18/react-antd-console) | [文档](https://doc.react-antd-console.site) | [在线预览](https://template.react-antd-console.site) | [拓展 pro 版在线预览](https://react-antd-console.site)