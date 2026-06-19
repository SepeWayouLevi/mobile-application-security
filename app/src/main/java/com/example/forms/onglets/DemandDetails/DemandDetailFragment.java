package com.example.forms.onglets.DemandDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forms.R;
import com.example.forms.models.Demand;

public class DemandDetailFragment extends Fragment {
    private static final String ARG_DEMAND = "arg_demand";
    private Demand demand;

    public static DemandDetailFragment newInstance(Demand demand) {
        DemandDetailFragment fragment = new DemandDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEMAND, demand);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            demand = (Demand) getArguments().getSerializable(ARG_DEMAND);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demand_detail, container, false);
        TextView nomDuDemandeur = view.findViewById(R.id.requester_name_demand_detail);
        TextView gamme = view.findViewById(R.id.product_line_demand_detail);
        TextView article = view.findViewById(R.id.article_demand_detail);
        TextView typeDeReference = view.findViewById(R.id.type_of_reference_demand_detail);
        TextView prixCatalogue = view.findViewById(R.id.catalog_price_demand_detail);
        TextView validationPricing  = view.findViewById(R.id.pricing_validation_demand_detail);
        TextView validationAchat = view.findViewById(R.id.purchase_validation_demand_detail);
        TextView validationAffairesReglementaires = view.findViewById(R.id.regulatory_affairs_validation_demand_detail);
        if (demand != null) {
            nomDuDemandeur.setText(demand.getRequesterName());
            gamme.setText(demand.getProductLine());
            article.setText(demand.getTypeOfArticle());
            typeDeReference.setText(demand.getTypeOfReference());
            prixCatalogue.setText(String.valueOf(demand.getPriceCatalog()));
            validationPricing.setText(demand.getPricingValidation());
            validationAchat.setText(demand.getPurchaseValidation());
            validationAffairesReglementaires.setText(demand.getAffRegValidation());
        }
        return view;
    }
}