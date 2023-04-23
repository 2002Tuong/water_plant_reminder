package com.example.waterplant.util;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterplant.adapter.PlantListAdapter;
import com.example.waterplant.viewmodel.PlantWaterViewModel;

public class PlantItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    OnItemTouchCallBack callBack;

    public PlantItemTouchHelperCallBack(OnItemTouchCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //In this method we will get movement of the recycleview item
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START ;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //callBack.onMove(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        callBack.onSwipe(viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        final View backgroundLayout = ((PlantListAdapter.PlantItemViewHolder) viewHolder).binding.foreLayout;
        getDefaultUIUtil().onDraw(c, recyclerView,backgroundLayout, dX,dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        final View foreLayout = ((PlantListAdapter.PlantItemViewHolder) viewHolder).binding.foreLayout;
        getDefaultUIUtil().onDrawOver(c, recyclerView,foreLayout, dX,dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //super.clearView(recyclerView, viewHolder);
        final View foreLayout = ((PlantListAdapter.PlantItemViewHolder) viewHolder).binding.foreLayout;
        getDefaultUIUtil().clearView(foreLayout);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null) {
            final View foreLayout = ((PlantListAdapter.PlantItemViewHolder) viewHolder).binding.foreLayout;
            getDefaultUIUtil().onSelected(foreLayout);
        }else {
            super.onSelectedChanged(viewHolder, actionState);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}

