package com.starorigins.stockify.widgetapp.portfolio.drag_drop

internal interface ItemTouchHelperAdapter {

  fun onItemMove(
    fromPosition: Int,
    toPosition: Int
  ): Boolean

  fun onItemDismiss(position: Int)
}