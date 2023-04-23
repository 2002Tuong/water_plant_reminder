package com.example.waterplant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.databinding.SearchItemBinding;

public class SearchListAdapter extends ListAdapter<Plant, SearchListAdapter.ViewHolder> {
    private OnItemClickListener callBack;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public SearchItemBinding binding;
        public ViewHolder(@NonNull SearchItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
        public void bind(Plant item) {
            binding.plantName.setText(item.getName());
        }
    }

    public SearchListAdapter(@NonNull DiffUtil.ItemCallback<Plant> diffCallback, OnItemClickListener callback) {
        super(diffCallback);
        this.callBack = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchListAdapter.ViewHolder(
                SearchItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plant item = getItem(position);
        holder.bind(item);
        holder.binding.getRoot().setOnClickListener(view -> {
            callBack.onClick(item);
        });
    }
}
