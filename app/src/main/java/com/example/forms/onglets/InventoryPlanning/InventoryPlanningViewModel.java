package com.example.forms.onglets.InventoryPlanning;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventoryPlanningViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public InventoryPlanningViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In the future, inventory & planning will create the reference directly in SAP");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
