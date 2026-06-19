package com.example.forms.onglets.GeneralInfos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GeneralInfosViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public GeneralInfosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is General infos fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
