package com.example.forms.onglets.myRequests;
import android.os.Bundle;
import android.util.Log;
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
import com.example.forms.adapters.MyRequestsAdapter;
import com.example.forms.api.ApiService;
import com.example.forms.api.RetrofitClient;
import com.example.forms.databinding.FragmentMyrequestsBinding;
import com.example.forms.models.Demand;
import com.example.forms.security.SecureAuthStore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyRequestsAdapter myRequestsAdapter;
    private List<Demand> demandList = new ArrayList<>();
    private MyRequestsViewModel myRequestsViewModel;
    private SwipeRefreshLayout swipeRefreshMyRequests;
    private ApiService apiService;
    private static long requestID;
    SecureAuthStore secureAuthStore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myrequests, container, false);
        recyclerView = view.findViewById(R.id.recyclerMyRequests);
        swipeRefreshMyRequests = view.findViewById(R.id.SwipeRefreshPricing);
        try {
            secureAuthStore = new SecureAuthStore(requireContext());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        apiService = RetrofitClient.getApiService(secureAuthStore);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myRequestsAdapter = new MyRequestsAdapter(
                demandList,
                getContext(),
                demand -> {
                    NavController navController = NavHostFragment.findNavController(this);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("arg_demand", demand);
                    navController.navigate(R.id.demandDetailFragment, bundle);
                },
                new MyRequestsAdapter.OnValidationActionListener() {
                    @Override
                    public void onValider(Demand demand, int position) {
                        demand.setStatusID("Awaiting");
                        String typeOfReference = demand.getTypeOfReference();
                        String pricingValidationStatus =  demand.getPricingValidation();
                        String affRegValidationStatus = demand.getAffRegValidation();
                        String purchaseValidationStatus = demand.getPurchaseValidation();
                        boolean marking = demand.isMarking();
                       if(
                               (
                                ("A".equals(typeOfReference)
                                ||"E".equals(typeOfReference)
                                ||"F".equals(typeOfReference)
                                ||"G".equals(typeOfReference)
                                ||"H".equals(typeOfReference)
                                )
                                && "Rejected".equals(pricingValidationStatus))
                                ||
                                ("A".equals(typeOfReference)
                                        || "E".equals(typeOfReference)
                                        || "F".equals(typeOfReference)
                                        || "G".equals(typeOfReference)
                                        || "H".equals(typeOfReference))
                                        && pricingValidationStatus == null && affRegValidationStatus == null && purchaseValidationStatus == null) {

                           Log.d("REQUEST STATUS ID ", demand.getStatusID());

                           apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID = response.body().getId();

                                    apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "request sent to pricing", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "failure while sending the request to pricing", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "request not sent try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                       else if (( ("B".equals(typeOfReference) || "C".equals(typeOfReference))
                                   && "Rejected".equals(pricingValidationStatus)
                                   && !marking)
                                   || (("B".equals(typeOfReference) || "C".equals(typeOfReference) )
                                   && pricingValidationStatus == null
                                   && purchaseValidationStatus == null
                                   && affRegValidationStatus == null && !marking)){
                           apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID =  response.body().getId() ;
                                    apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "request sent to pricing", Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "error while sending the request to pricing", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "request not sent try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (
                                (("B".equals(typeOfReference) || "C".equals(typeOfReference)) && "Rejected".equals(pricingValidationStatus) && "Rejected".equals(affRegValidationStatus))
                                     ||
                               (("B".equals(typeOfReference) || "C".equals(typeOfReference)) && pricingValidationStatus == null  && affRegValidationStatus == null && purchaseValidationStatus == null && marking) ){
                            apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID = response.body().getId();
                                    apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "request sent to pricing", Toast.LENGTH_SHORT).show();
                                            apiService.updateValidationAffairesReglementaires(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast.makeText(getContext(), "request sent to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(getContext(), "fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "fail to send the request to pricing", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "fail to send the request successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (("B".equals(typeOfReference)  || "C".equals(typeOfReference)) && "Rejected".equals(pricingValidationStatus) && ("Approved".equals(affRegValidationStatus)  || "Awaiting".equals(affRegValidationStatus))){
                            apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID =  response.body().getId();
                                    apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "updating successfuly the column reg aff", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "fail to update successfuly the column reg aff", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else if(("B".equals(typeOfReference)  || "C".equals(typeOfReference)) && ("Approved".equals(pricingValidationStatus) || "Awaiting".equals(pricingValidationStatus)) && "Rejected".equals(affRegValidationStatus)){
                            apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID =  response.body().getId();
                                    apiService.updateValidationAffairesReglementaires(requestID , "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "request successfuly send to regulatory affairs", Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if(("D".equals(typeOfReference) && ("Awaiting".equals(purchaseValidationStatus)
                                 || "Approved".equals(purchaseValidationStatus))
                                 && "Rejected".equals(pricingValidationStatus))

                       ){
                           apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    String regulatoryAffairsValidation = response.body().getAffRegValidation();
                                    boolean isMarking = response.body().isMarking();
                                    requestID =  response.body().getId();
                                    apiService.updateValidationPricing(requestID ,  "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "Request sent to pricing", Toast.LENGTH_SHORT).show();
                                            if( regulatoryAffairsValidation != null && isMarking  && "Rejected".equals(regulatoryAffairsValidation)){
                                                apiService.updateValidationAffairesReglementaires(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                        Toast.makeText(getContext(), "Request sent to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                    }
                                                    @Override
                                                    public void onFailure(Call<Void> call, Throwable t) {
                                                        Toast.makeText(getContext(), "Fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Fail to send the request sent to pricing", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "Fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if(
                                "D".equals(typeOfReference)
                                && "Rejected".equals(purchaseValidationStatus)
                                && ("Approved".equals(pricingValidationStatus) || "Awaiting".equals(pricingValidationStatus))

                        ){
                           apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID =  response.body().getId();
                                    String regulatoryAffairsValidation  =  response.body().getAffRegValidation();
                                    boolean isMarking = response.body().isMarking();
                                    apiService.updateValidationAchat(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "Request send to purchase", Toast.LENGTH_SHORT).show();
                                            if(isMarking && regulatoryAffairsValidation.equals("Rejected")){
                                                apiService.updateValidationAffairesReglementaires(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                        Toast.makeText(getContext(), "Request send to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                    }
                                                    @Override
                                                    public void onFailure(Call<Void> call, Throwable t) {
                                                        Toast.makeText(getContext(), "Fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Fail to send the request to purchase", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "Fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if(
                                "D".equals(typeOfReference)
                                && ( ("Awaiting".equals(purchaseValidationStatus)
                                ||  "Approved".equals(purchaseValidationStatus)))
                                && "Approved".equals(pricingValidationStatus)
                                && "Rejected".equals(affRegValidationStatus)){
                           apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID = response.body().getId();
                                    apiService.updateValidationAffairesReglementaires(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "Send request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "Fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if (
                                "D".equals(typeOfReference)
                                && "Rejected".equals(purchaseValidationStatus)
                                && "Rejected".equals(pricingValidationStatus)){

                           apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID = response.body().getId();
                                    boolean isMarking =  response.body().isMarking();
                                    String regulatoryAffairsValidationStatus = response.body().getAffRegValidation();
                                    apiService.updateValidationAchat(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "Request sent to purchase", Toast.LENGTH_SHORT).show();
                                            apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast.makeText(getContext(), "Request sent to pricing", Toast.LENGTH_SHORT).show();
                                                    if(isMarking && regulatoryAffairsValidationStatus.equals("Awaiting")){
                                                        apiService.updateValidationAffairesReglementaires(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                            @Override
                                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                                Toast.makeText(getContext(), "Request sent to regulatory affairs", Toast.LENGTH_SHORT).show();

                                                            }

                                                            @Override
                                                            public void onFailure(Call<Void> call, Throwable t) {
                                                                Toast.makeText(getContext(), "Fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(getContext(), "Fail to send the request to pricing", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Fail to send the request sent to purchase", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "Fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if("D".equals(typeOfReference) && purchaseValidationStatus == null && pricingValidationStatus == null && !marking){
                            apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID =  response.body().getId();
                                    apiService.updateValidationAchat(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "Request sent to purchase", Toast.LENGTH_SHORT).show();
                                            apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast.makeText(getContext(), "Request sent to pricing", Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(getContext(), "Fail to sent the request to pricing", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Fail to sent the request", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                }
                            });
                       } else if("D".equals(typeOfReference) && purchaseValidationStatus == null && pricingValidationStatus == null && affRegValidationStatus == null && marking){
                            apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                                @Override
                                public void onResponse(Call<Demand> call, Response<Demand> response) {
                                    requestID =  response.body().getId();
                                    apiService.updateValidationAchat(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(getContext(), "Request sent to purchase", Toast.LENGTH_SHORT).show();
                                            apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast.makeText(getContext(), "Request sent to pricing", Toast.LENGTH_SHORT).show();
                                                    apiService.updateValidationAffairesReglementaires(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                                        @Override
                                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                                            Toast.makeText(getContext(), "Request sent to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Void> call, Throwable t) {
                                                            Toast.makeText(getContext(), "Fail to send the request to regulatory affairs", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(getContext(), "Fail to send the request to pricing", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(getContext(), "Fail to send the request sent to purchase", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                @Override
                                public void onFailure(Call<Demand> call, Throwable t) {
                                    Toast.makeText(getContext(), "Fail to send the request", Toast.LENGTH_SHORT).show();
                                }
                            });
                       } else {
                           Toast.makeText(getContext(), "Verify your input", Toast.LENGTH_SHORT).show();

                       }
                        demand.setSend(true); // ← marque comme Envoyé
                        myRequestsAdapter.notifyItemChanged(position); // ← met à jour la vue
                    }
                    @Override
                    public void onRefuser(Demand demand, int position) {
                        Bundle args = new Bundle();
                        args.putSerializable("demand", demand);
                        NavController nav = NavHostFragment.findNavController(MyRequestsFragment.this);
                        nav.navigate(R.id.editFragment, args);

                    }
                }
        );

        recyclerView.setAdapter(myRequestsAdapter);

        myRequestsViewModel = new ViewModelProvider(this).get(MyRequestsViewModel.class);
        observeMyRequests();

        swipeRefreshMyRequests.setOnRefreshListener(() -> {
            try {
                myRequestsViewModel.loadMyRequests();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        });
        return view;
    }

    private void observeMyRequests() {
        myRequestsViewModel.getFormulaireByEmail().observe(getViewLifecycleOwner(), demands -> {
            demandList.clear();
            if (demands != null) demandList.addAll(demands);
            myRequestsAdapter.notifyDataSetChanged();
            swipeRefreshMyRequests.setRefreshing(false); // Arrête l'animation de rafraîchissement
        });
    }



    @Override
    public void onStart() {
        super.onStart();
       // myRequestsViewModel.loadMyRequests(); // Rafraîchit automatiquement à chaque affichage du fragment
    }

    @Override
    public void onResume() {
        super.onResume();

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
