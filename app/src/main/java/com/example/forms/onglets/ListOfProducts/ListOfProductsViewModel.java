package com.example.forms.onglets.ListOfProducts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListOfProductsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ListOfProductsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("There will be a list of products here");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
