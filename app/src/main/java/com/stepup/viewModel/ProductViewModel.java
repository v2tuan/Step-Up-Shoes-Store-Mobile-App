package com.stepup.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stepup.model.ProductCard;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private final MutableLiveData<List<ProductCard>> productList = new MutableLiveData<>(new ArrayList<>());

    public void setProductList(List<ProductCard> products) {
        productList.setValue(products);
    }

    public MutableLiveData<List<ProductCard>> getProductList() {
        return productList;
    }
}
