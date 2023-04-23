package com.example.waterplant.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.waterplant.R;
import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.databinding.PlantListItemBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlantListAdapter extends ListAdapter<Plant, PlantListAdapter.PlantItemViewHolder> {
    private OnItemClickListener onClick;
    public PlantListAdapter(@NonNull DiffUtil.ItemCallback<Plant> diffCallback, OnItemClickListener onClick) {
        super(diffCallback);
        this.onClick = onClick;
    }

    public class PlantItemViewHolder extends RecyclerView.ViewHolder {
        public final PlantListItemBinding binding;
        private final Context context;

        public PlantItemViewHolder(@NonNull PlantListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();
            binding.plantThumbnail.setClipToOutline(true);
        }
        public void binding(Plant plant) {
            binding.plantName.setText(plant.getName());
            binding.plantWater.setText(context.getString(R.string.water, Integer.parseInt(plant.getWater())));
            if(plant.getThumbnailUrl().isEmpty()) {
                binding.plantThumbnail.setImageResource(R.drawable.image_background);
            }else {
                Glide.with(binding.getRoot())
                        .load(Uri.parse(plant.getThumbnailUrl()))
                        .into(binding.plantThumbnail);
            }
            binding.getRoot().setOnClickListener((view) -> {
                onClick.onClick(plant);
            });
            isButtonClick();
        }
        public void isButtonClick() {
            binding.isWateringCheckBtn.setOnClickListener(view -> {

                binding.isWateringCheckBtn.setImageResource(R.drawable.drop_water_to_done);
                AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) binding.isWateringCheckBtn.getDrawable();
                animatedVectorDrawable.start();

                binding.isWateringCheckBtn.setImageResource(R.drawable.done_to_drop_water);
                AnimatedVectorDrawable animatedVectorDrawable1 = (AnimatedVectorDrawable) binding.isWateringCheckBtn.getDrawable();
                animatedVectorDrawable1.start();

            });
        }
    }

    @NonNull
    @Override
    public PlantItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlantListItemBinding binding = PlantListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlantItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantItemViewHolder holder, int position) {
        Plant plant = getItem(position);
        holder.binding(plant);


    }

    public Plant getItemAtPosition(int position) {
        return getItem(position);
    }

    public static class DiffCallBack extends DiffUtil.ItemCallback<Plant> {
        @Override
        public boolean areItemsTheSame(@NonNull Plant oldItem, @NonNull Plant newItem) {
            return oldItem == newItem;
        }


        @Override
        public boolean areContentsTheSame(@NonNull Plant oldItem, @NonNull Plant newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }
    }

    public void setCallBack(OnItemClickListener callBack) {
        onClick = callBack;
    }
}
