package com.example.forms.onglets.edit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditViewModel extends ViewModel {
    public final MutableLiveData<String> typeOfReference = new MutableLiveData<>();
    public final MutableLiveData<String> requesterName = new MutableLiveData<>();
    public final MutableLiveData<String> productLine = new MutableLiveData<>();
    public final MutableLiveData<String> typeOfArticle = new MutableLiveData<>();
    public final MutableLiveData<String> priceCatalog = new MutableLiveData<>();
    public final MutableLiveData<String> productClassificationDescription = new MutableLiveData<>();
    public final MutableLiveData<Boolean> marking = new MutableLiveData<>();
    public final MutableLiveData<String> typeOfMarking = new MutableLiveData<>();
}
