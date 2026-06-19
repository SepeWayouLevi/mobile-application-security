package com.example.forms.onglets.ListOfProducts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.forms.databinding.FragmentListofProductsBinding;

public class ListOfProductsFragment extends Fragment {
    private FragmentListofProductsBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ListOfProductsViewModel listOfProductsViewModel =
                new ViewModelProvider(this).get(ListOfProductsViewModel.class);
        binding = FragmentListofProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textForListOfProducts;
        listOfProductsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
