package com.azhu.v2ex.ui.staggeredgrid

import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.ui.unit.Density

/**
 * @author: Jerry
 * @date: 2024-10-26 15:53
 * @version: 1.0.0
 */
class FixedAdaptive(private val count: Int, private val itemCount: Int) : StaggeredGridCells {

    init {
        require(count > 0) { "grid with no rows/columns" }
    }

    override fun Density.calculateCrossAxisCellSizes(availableSize: Int, spacing: Int): IntArray {
        val slotCount = if (itemCount <= 5) 1 else if (itemCount <= 10) 2 else count
        return calculateCellsCrossAxisSizeImpl(availableSize, slotCount, spacing)
    }

    private fun calculateCellsCrossAxisSizeImpl(
        gridSize: Int,
        slotCount: Int,
        spacing: Int
    ): IntArray {
        val gridSizeWithoutSpacing = gridSize - spacing * (slotCount - 1)
        val slotSize = gridSizeWithoutSpacing / slotCount
        val remainingPixels = gridSizeWithoutSpacing % slotCount
        val result = IntArray(slotCount) {
            if (slotSize < 0) {
                0
            } else {
                slotSize + if (it < remainingPixels) 1 else 0
            }
        }
        return result
    }
}