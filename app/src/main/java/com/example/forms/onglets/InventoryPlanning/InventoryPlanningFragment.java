package com.example.forms.onglets.InventoryPlanning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.forms.databinding.FragmentInventoryplanningBinding;



public class InventoryPlanningFragment extends Fragment {
    private FragmentInventoryplanningBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        InventoryPlanningViewModel inventoryPlanningViewModel =
                new ViewModelProvider(this).get(InventoryPlanningViewModel.class);
        binding = FragmentInventoryplanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textInventoryPlanning;
        inventoryPlanningViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
