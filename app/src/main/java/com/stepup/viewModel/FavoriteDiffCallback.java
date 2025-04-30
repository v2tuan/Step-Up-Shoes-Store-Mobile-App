package com.stepup.viewModel;

import androidx.recyclerview.widget.DiffUtil;

import com.stepup.model.Favorite;

import java.util.List;

public class FavoriteDiffCallback extends DiffUtil.Callback{
    private List<Favorite> oldList;
    private List<Favorite> newList;

    public FavoriteDiffCallback(List<Favorite> oldList, List<Favorite> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Favorite oldItem = oldList.get(oldItemPosition);
        Favorite newItem = newList.get(newItemPosition);
        return oldItem.getTitle().equals(newItem.getTitle()) &&
                oldItem.getPrice() == newItem.getPrice() &&
                oldItem.getColor().getId() == newItem.getColor().getId();
    }
}
