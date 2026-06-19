package com.example.forms.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.forms.R;
import com.example.forms.models.Demand;

import java.util.List;

public class MyRequestsAdapter extends DemandAdapter {

    public MyRequestsAdapter(List<Demand> demandList,
                             Context context,
                             DemandAdapter.OnItemClickListener listener,
                             DemandAdapter.OnValidationActionListener actionListener) {
        super(demandList, context, listener, actionListener);
    }

    @NonNull
    @Override
    public DemandAdapter.DemandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_requests, parent, false);
        return new MyRequestsAdapter.MyRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemandAdapter.DemandViewHolder holder, int position) {
        Demand demand = getDemandList().get(position);
        MyRequestViewHolder h = (MyRequestViewHolder) holder;
        h.bind(demand);
        h.itemView.setOnClickListener(v -> {
            OnItemClickListener l = getListener();
            int pos = h.getBindingAdapterPosition();
            if (l != null && pos != RecyclerView.NO_POSITION) {
                l.onItemClick(demand);
            }
        });
        h.buttonSend.setOnClickListener(v -> {
            OnValidationActionListener al = getActionListener();
            int pos = h.getBindingAdapterPosition();
            if (al != null && pos != RecyclerView.NO_POSITION) {
                al.onValider(demand, pos);
            }
        });

        h.buttonEdit.setOnClickListener(v -> {
            OnValidationActionListener al = getActionListener();
            int pos = h.getBindingAdapterPosition();
            if (al != null && pos != RecyclerView.NO_POSITION) {
                al.onRefuser(demand, pos);
            }
        });
    }

    static class MyRequestViewHolder extends DemandAdapter.DemandViewHolder {
        Button buttonSend, buttonEdit;
        TextView requesterNameOfMyRequest,
                productLineOfMyRequest,
                articleOfMyRequest,
                typeOfReferenceOfMyRequest,
                priceCatalogOfMyRequest,
                ProductClassificationDescriptionOfMyRequest,
                StatusIDOfMyRequest;

        public MyRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonSend = itemView.findViewById(R.id.btnSend);
            buttonEdit = itemView.findViewById(R.id.btnEdit);
            requesterNameOfMyRequest = itemView.findViewById(R.id.requester_name_of_myRequest);
            productLineOfMyRequest = itemView.findViewById(R.id.productline_of_myRequest);
            articleOfMyRequest = itemView.findViewById(R.id.article_of_myRequest);
            typeOfReferenceOfMyRequest = itemView.findViewById(R.id.type_of_reference_of_myRequest);
            priceCatalogOfMyRequest =  itemView.findViewById(R.id.price_catalog_of_myRequest);
            ProductClassificationDescriptionOfMyRequest =  itemView.findViewById(R.id.product_classification_description_of_myRequest);
            StatusIDOfMyRequest = itemView.findViewById(R.id.statusID_of_myRequest);
        }

        void bind(final Demand demand) {
            requesterNameOfMyRequest.setText(demand.getRequesterName());
            productLineOfMyRequest.setText(demand.getProductLine());
            articleOfMyRequest.setText(demand.getTypeOfArticle());
            typeOfReferenceOfMyRequest.setText(demand.getTypeOfReference());
            priceCatalogOfMyRequest.setText(String.format("%.2f", demand.getPriceCatalog()));
            ProductClassificationDescriptionOfMyRequest.setText(demand.getProductClassificationDescription());
            StatusIDOfMyRequest.setText(demand.getStatusID());

            if ("Rejected".equals(demand.getPricingValidation())
                    || "Rejected".equals(demand.getAffRegValidation())
                    || "Rejected".equals(demand.getPurchaseValidation())) {
                buttonSend.setVisibility(View.VISIBLE);
                buttonEdit.setVisibility(View.VISIBLE);
            } else if ("Awaiting".equals(demand.getStatusID())){
                buttonSend.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.GONE);

            } else if("Approved".equals(demand.getPricingValidation()) && demand.getTypeOfReference().equals("A")){
                buttonSend.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.GONE);
            }
            else {
                buttonSend.setVisibility(View.VISIBLE);
                buttonEdit.setVisibility(View.VISIBLE);
            }
        }
    }
}
