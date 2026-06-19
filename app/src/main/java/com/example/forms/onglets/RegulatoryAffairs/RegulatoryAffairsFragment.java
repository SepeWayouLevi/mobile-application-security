package com.example.forms.onglets.RegulatoryAffairs;

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
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegulatoryAffairsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;
    private List<Demand> demandList = new ArrayList<>();
    private ApiService apiService;
    SecureAuthStore secureAuthStore;

    private RegulatoryAffairsViewModel regulatoryAffairsViewModel;
    private SwipeRefreshLayout swipeRefreshLayoutAffairesReglementaires;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regulatory_affairs, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAffairesReglementaires);
        swipeRefreshLayoutAffairesReglementaires = view.findViewById(R.id.SwipeRefreshAffairesReglementaires);
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
                    NavController navController = NavHostFragment.findNavController(this);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("arg_demand", demand);
                    navController.navigate(R.id.demandDetailFragment, bundle);
                },
                new DemandAdapter.OnValidationActionListener() {
                    @Override
                    public void onValider(Demand demand, int position) {
                        Call<Void> updatingColumnAffairesReglementaires = apiService.updateValidationAffairesReglementaires(demand.getId(), "Approved");
                        updatingColumnAffairesReglementaires.enqueue(new Callback<Void>() {
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
                        Call<Void> updatingColumnAffairesReglementaires = apiService.updateValidationAffairesReglementaires(demand.getId(), "Rejected");
                        updatingColumnAffairesReglementaires.enqueue(new Callback<Void>() {
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
                                Toast.makeText(getContext(), "Connection failure" + t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
        );

        recyclerView.setAdapter(demandAdapter);
        regulatoryAffairsViewModel = new ViewModelProvider(this).get(RegulatoryAffairsViewModel.class);
        observeAffairesReglementairesDemands();

        swipeRefreshLayoutAffairesReglementaires.setOnRefreshListener(() -> {
            try {
                regulatoryAffairsViewModel.loadDemandForRegulatoryAffairs();  // Recharge depuis l'API
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        });
        return  view;
    }

    private void observeAffairesReglementairesDemands() {
        regulatoryAffairsViewModel.getFormulaireWithMarking().observe(getViewLifecycleOwner(), demands -> {
            demandList.clear();
            if (demands != null) demandList.addAll(demands);
            demandAdapter.notifyDataSetChanged();
            swipeRefreshLayoutAffairesReglementaires.setRefreshing(false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            regulatoryAffairsViewModel.loadDemandForRegulatoryAffairs();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            regulatoryAffairsViewModel.loadDemandForRegulatoryAffairs();
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
        recyclerView.setAdapter(null);
    }


}



