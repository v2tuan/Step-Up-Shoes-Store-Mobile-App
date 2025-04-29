package com.stepup.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stepup.model.Favorite;
import com.stepup.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteViewModel extends ViewModel {
    private Map<Long, Product> productCache = new HashMap<>();
    private MutableLiveData<List<Favorite>> favoriteItems = new MutableLiveData<>();

    private Map<Long, Boolean> favoriteStatusCache = new HashMap<>();
    private MutableLiveData<Map<Long, Boolean>> favoriteStatusLiveData = new MutableLiveData<>(new HashMap<>());
    public void cacheProduct(long colorId, Product product) {
        productCache.put(colorId, product);
    }

    public Product getProductByColorId(long colorId) {
        return productCache.get(colorId);
    }

    public LiveData<List<Favorite>> getFavoriteItems() {
        return favoriteItems;
    }

    public void setFavoriteItems(List<Favorite> items) {
        favoriteItems.setValue(items);
    }
    public void addFavoriteItem(Favorite favorite) {
        List<Favorite> currentItems = new ArrayList<>(favoriteItems.getValue());
        if (!currentItems.contains(favorite)) {
            currentItems.add(favorite);
            favoriteItems.setValue(currentItems);
        }
        setFavoriteStatus(favorite.getId(), true);
    }

    public void removeFavoriteItem(long id) {
        List<Favorite> currentItems = new ArrayList<>(favoriteItems.getValue());
        int indexToRemove = -1;
        for (int i = 0; i < currentItems.size(); i++) {
            if (currentItems.get(i).getId() == id) {
                indexToRemove = i;
                break;
            }
        }
        if (indexToRemove >= 0) {
            currentItems.remove(indexToRemove);
            favoriteItems.setValue(currentItems);
        }
        setFavoriteStatus(id, false);
    }

    public void setFavoriteStatus(long id, boolean isFavorite) {
        favoriteStatusCache.put(id, isFavorite);
        favoriteStatusLiveData.setValue(new HashMap<>(favoriteStatusCache));
    }

    public Boolean getFavoriteStatus(long id) {
        return favoriteStatusCache.get(id);
    }

    public LiveData<Map<Long, Boolean>> getFavoriteStatusLiveData() {
        return favoriteStatusLiveData;
    }

}
