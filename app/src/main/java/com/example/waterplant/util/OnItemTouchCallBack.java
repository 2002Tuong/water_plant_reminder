package com.example.waterplant.util;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemTouchCallBack {

    void onMove(int bindingAdapterPosition, int bindingAdapterPosition1);

    void onSwipe(RecyclerView.ViewHolder viewHolder);
}
