package com.example.forms.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forms.BuildConfig;
import com.example.forms.R;
import com.example.forms.models.Demand;
import java.util.List;

public class DemandAdapter extends RecyclerView.Adapter<DemandAdapter.DemandViewHolder> {

    // Interface pour le clic
    public interface OnItemClickListener {
        void onItemClick(Demand demand);
    }
    public interface OnValidationActionListener {
        void onValider(Demand demand, int position);
        void onRefuser(Demand demand, int position);
    }
    private OnValidationActionListener actionListener;
    private List<Demand> demandList;
    private Context context;
    private OnItemClickListener listener;

    // Nouveau constructeur avec listener
    public DemandAdapter(List<Demand> demandList, Context context, OnItemClickListener listener, OnValidationActionListener actionListener ) {
        this.demandList = demandList != null ? demandList : new java.util.ArrayList<>();
        this.context = context;
        this.listener = listener;
        this.actionListener = actionListener;

    }

    @NonNull
    @Override
    public DemandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demand, parent, false);
        return new DemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemandViewHolder holder, int position) {
        Demand demand = demandList.get(position);
        holder.bind(demand);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(demand);
        });

        holder.buttonAccept.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (actionListener != null && pos != RecyclerView.NO_POSITION) {
                actionListener.onValider(demand, pos);
            }
        });

        holder.buttonReject.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (actionListener != null && pos != RecyclerView.NO_POSITION) {
                actionListener.onRefuser(demand, pos);
            }
        });

        if(actionListener == null) {
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonReject.setVisibility(View.GONE);
        } else {
            holder.buttonAccept.setVisibility(View.VISIBLE);
            holder.buttonReject.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public int getItemCount() {
        return demandList.size();
    }

    // Pour rafraîchir la liste si besoin
    public void setDemandList(List<Demand> newList) {
        this.demandList = newList != null ? newList : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    static class DemandViewHolder extends RecyclerView.ViewHolder {
        Button buttonAccept, buttonReject;
        TextView requesterName, productLine, article, typeOfReference, priceCatalog, productClassificationDescription, StatusID ;

        public DemandViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonAccept = itemView.findViewById(R.id.btnAccept);
            buttonReject = itemView.findViewById(R.id.btnReject);
            requesterName = itemView.findViewById(R.id.requester_name_cardviewDemand);
            productLine = itemView.findViewById(R.id.productline_cardviewDemand);
            article = itemView.findViewById(R.id.article_cardviewDemand);
            typeOfReference = itemView.findViewById(R.id.type_of_reference_cardviewDemand);
            priceCatalog =  itemView.findViewById(R.id.price_catalog_cardviewDemand);
            productClassificationDescription =  itemView.findViewById(R.id.product_classification_description_cardViewDemand);

        }

        void bind(final Demand demand) {
            requesterName.setText(demand.getRequesterName());
            productLine.setText(demand.getProductLine());
            article.setText(demand.getTypeOfArticle());
            typeOfReference.setText(demand.getTypeOfReference());
            priceCatalog.setText(String.format("%.2f", demand.getPriceCatalog()));
            productClassificationDescription.setText(demand.getProductClassificationDescription());
            if(BuildConfig.DEBUG){
                Log.d("ADAPTER", "bind : " + demand.getRequesterName());
            }
        }
    }

    public List<Demand> getDemandList() {
        return demandList;
    }

    public OnValidationActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnValidationActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
