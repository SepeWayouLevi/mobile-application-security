package com.example.forms.onglets.purchase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.forms.api.ApiService;
import com.example.forms.api.RetrofitClient;
import com.example.forms.models.Demand;
import com.example.forms.security.SecureAuthStore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Demand>> demands = new MutableLiveData<>();
    private final SecureAuthStore authStore;
    private final ApiService api;
    private static final String PurchaseReferenceType = "D";
    public PurchaseViewModel(@NonNull Application app) throws GeneralSecurityException, IOException {
        super(app);
        authStore = new SecureAuthStore(app);
        api = RetrofitClient.getApiService(authStore);
        loadDemandForPurchase();
    }
    public LiveData<List<Demand>> getFormulaireWithRefD(){
        return demands;
    }
    public void loadDemandForPurchase() throws GeneralSecurityException {
        api.getFormulaireWithRefD(authStore.getEmailFromToken(authStore.getAccessToken()), PurchaseReferenceType).enqueue(new Callback<List<Demand>>() {
            @Override
            public void onResponse(Call<List<Demand>> call, Response<List<Demand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Demand> filteredDemands = new ArrayList<>();
                    List<Demand> allDemands = response.body();
                    for (Demand d : allDemands) {
                        if ("Awaiting".equals(d.getPurchaseValidation())) {
                            filteredDemands.add(d);
                        }
                    }
                    demands.setValue(filteredDemands);
                } else {
                    demands.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Demand>> call, Throwable t) {
                demands.setValue(new ArrayList<>());
            }
        });
    }
}
