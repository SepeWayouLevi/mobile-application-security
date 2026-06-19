package com.example.forms.onglets.pricing;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.forms.R;
import com.example.forms.adapters.DemandAdapter;
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

public class PricingFragment extends Fragment {
    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;
    private List<Demand> demandList = new LinkedList<>();
    private PricingViewModel pricingViewModel;
    private SwipeRefreshLayout swipeRefreshLayoutPricing;
    private ApiService apiService;
    SecureAuthStore secureAuthStore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pricing, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPricing);
        swipeRefreshLayoutPricing = view.findViewById(R.id.SwipeRefreshPricing);
        try {
            secureAuthStore = new SecureAuthStore(requireContext());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String token = null;
        try {
            token = secureAuthStore.getAccessToken();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        apiService = RetrofitClient.getApiService(secureAuthStore);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        demandAdapter = new DemandAdapter(
                demandList,
                getContext(),
                demand -> {
                    // Navigation vers le détail
                    NavController navController = NavHostFragment.findNavController(this);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("arg_demand", demand);
                    navController.navigate(R.id.demandDetailFragment, bundle);
                },
                new DemandAdapter.OnValidationActionListener() {
                    @Override
                    public void onValider(Demand demand, int position) {
                        Call<Void> updatingColumnValidationPricing = apiService.updateValidationPricing(demand.getId(), "Approved");
                        updatingColumnValidationPricing.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                                    demandList.remove(position);
                                    demandAdapter.notifyItemRemoved(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), "Connection failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onRefuser(Demand demand, int position) {
                        Call<Void> updatingColumnValidationPricing = apiService.updateValidationPricing(demand.getId(), "Rejected");
                        updatingColumnValidationPricing.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Request rejected", Toast.LENGTH_SHORT).show();
                                    demandList.remove(position);
                                    demandAdapter.notifyItemRemoved(position);
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), "Connection failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
        );
        recyclerView.setAdapter(demandAdapter);
        pricingViewModel = new ViewModelProvider(this).get(PricingViewModel.class);
        observePricingDemands();
        swipeRefreshLayoutPricing.setOnRefreshListener(() -> {
            try {
                pricingViewModel.loadDemandsForPricing();  // Recharge depuis l'API
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        });
        return view;
    }
    private void observePricingDemands() {
        pricingViewModel.getFormulaireWithAPrice().observe(getViewLifecycleOwner(), demands -> {
                demandList.clear();
                if (demands != null) demandList.addAll(demands);
                demandAdapter.notifyDataSetChanged();
                swipeRefreshLayoutPricing.setRefreshing(false); // Arrête l'animation de rafraîchissement
            });

        }



    @Override
    public void onStart() {
        super.onStart();
        try {
            pricingViewModel.loadDemandsForPricing(); // Rafraîchit automatiquement à chaque affichage du fragment
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            pricingViewModel.loadDemandsForPricing(); // Recharge automatiquement la liste à chaque retour sur Pricing
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }



    @Override
    public void onDestroyView() {
    super.onDestroyView();
    recyclerView.setAdapter(null);  // Libère l'adapter pour éviter les fuites mémoire
}

}

