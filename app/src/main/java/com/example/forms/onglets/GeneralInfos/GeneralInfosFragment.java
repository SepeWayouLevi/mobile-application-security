package com.example.forms.onglets.GeneralInfos;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.forms.databinding.FragmentGeneralInfosBinding;

public class GeneralInfosFragment extends Fragment {
    private FragmentGeneralInfosBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        GeneralInfosViewModel generalInfosViewModel =
                new ViewModelProvider(this).get(GeneralInfosViewModel.class);
        binding = FragmentGeneralInfosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGeneralInfos;
        generalInfosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
