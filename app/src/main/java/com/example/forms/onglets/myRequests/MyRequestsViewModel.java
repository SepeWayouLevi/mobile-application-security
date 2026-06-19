package com.example.forms.onglets.myRequests;

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
import java.util.LinkedList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MyRequestsViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Demand>> demands = new MutableLiveData<>();
    private final SecureAuthStore authStore;
    private final ApiService api;

    public MyRequestsViewModel(@NonNull Application app) throws GeneralSecurityException, IOException {
        super(app);
        authStore = new SecureAuthStore(app);
        api = RetrofitClient.getApiService(authStore);
        loadMyRequests();
    }

    public LiveData<List<Demand>> getFormulaireByEmail() { return demands; }

    public void loadMyRequests() throws GeneralSecurityException {
            String email = authStore.getEmailFromToken(authStore.getAccessToken());
            api.getFormulaireByEmail(email).enqueue(new Callback<List<Demand>>() {
                @Override public void onResponse(Call<List<Demand>> call, Response<List<Demand>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Demand> myListOfRequests = new LinkedList<>();
                        for (Demand myRequest : response.body()) {
                            try {
                                if (authStore.getEmailFromToken(authStore.getAccessToken()).equals(myRequest.getEmail())) myListOfRequests.add(myRequest);
                            } catch (GeneralSecurityException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        demands.setValue(myListOfRequests);
                    } else {
                        demands.setValue(new LinkedList<>());
                    }
                }
                @Override public void onFailure(Call<List<Demand>> call, Throwable t) {
                    demands.setValue(new LinkedList<>());
                }
            });
    }
}
