package com.example.forms.onglets.AllDemands;

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

public class AllDemandsViewModel  extends AndroidViewModel {
    private MutableLiveData<List<Demand>> demands = new MutableLiveData<>();
    private final SecureAuthStore authStore;
    private final ApiService api;

    public AllDemandsViewModel(@NonNull Application app) throws GeneralSecurityException, IOException {
        super(app);
        authStore = new SecureAuthStore(app);
        api = RetrofitClient.getApiService(authStore);
        loadDemands();
    }

    public LiveData<List<Demand>> getDemandsByCountry(){
        return demands ;
    }

    public void loadDemands() throws GeneralSecurityException {
            api.getDemandsByCountry(authStore.getEmailFromToken(authStore.getAccessToken())).enqueue(new Callback<List<Demand>>() {
                @Override
                public void onResponse(Call<List<Demand>> call, Response<List<Demand>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        demands.setValue(response.body());
                    } else {
                        demands.setValue(new LinkedList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<Demand>> call, Throwable t) {
                    demands.setValue(new LinkedList<>());

                }
            });
    }
}
