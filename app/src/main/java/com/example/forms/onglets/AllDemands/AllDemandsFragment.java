package com.example.forms.onglets.AllDemands;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.forms.models.Demand;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class AllDemandsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;
    private List<Demand> demandList = new ArrayList<>();
    private AllDemandsViewModel allDemandsViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alldemands, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


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

                null
        );

        recyclerView.setAdapter(demandAdapter);

        allDemandsViewModel = new ViewModelProvider(this).get(AllDemandsViewModel.class);
        observeDemands();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                allDemandsViewModel.loadDemands();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        });

        return view;
    }

    private void observeDemands() {
        allDemandsViewModel.getDemandsByCountry().observe(getViewLifecycleOwner(), demands -> {
            demandList.clear();
            if (demands != null) demandList.addAll(demands);
            demandAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            allDemandsViewModel.loadDemands();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            allDemandsViewModel.loadDemands();
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
