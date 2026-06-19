package com.example.forms.onglets.forms;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.forms.BuildConfig;
import com.example.forms.R;
import com.example.forms.api.ApiService;
import com.example.forms.api.RetrofitClient;
import com.example.forms.models.Demand;
import com.example.forms.security.SecureAuthStore;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormsFragment extends Fragment  {
    private SecureAuthStore secureAuthStore;
    private ApiService apiService;

    private static long requestID;
    private FormsFragmentViewModel viewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            secureAuthStore = new SecureAuthStore(requireContext());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        apiService = RetrofitClient.getApiService(secureAuthStore);

        View view = inflater.inflate(R.layout.fragment_forms, container, false);
        Spinner spinnerTypeOfReference = view.findViewById(R.id.spinner_type_of_reference);
        TextView markingTextView = view.findViewById(R.id.text_marking);
        TextView priceCatalogTextView =  view.findViewById(R.id.price_catalog_text_view);
        EditText editRequesterName = view.findViewById(R.id.requester_name);
        Spinner spinnerProductLine = view.findViewById(R.id.spinner_productLine);
        Spinner spinnerTypeOfArticle = view.findViewById(R.id.spinner_type_of_article);
        EditText editPriceCatalog  = view.findViewById(R.id.price_catalog);
        EditText editProductClassificationDescription = view.findViewById(R.id.product_classification);
        Button myButtonSend = view.findViewById(R.id.button_send);

        Button myButtonSave=view.findViewById(R.id.button_save);
        RadioGroup radioGroupMarking  = view.findViewById(R.id.radioGroup_Marking);
        Spinner spinnerOptionsMarking  = view.findViewById(R.id.options_marking);

        String[] optionsMarking = {"IVDD", "IVDR", "MDD" , "MDR", "CE", "FDA" , "IKCA"};
        String[] typeOfReference = {"A", "B", "C" , "D", "E", "F" , "G", "H"};
        String[] productLine = {"ProductLine 1", "ProductLine 2", "ProductLine 3"};
        String[] typeOfArticle = {"Type X", "Type Y", "Type Z"};

        ArrayAdapter<String> adapterMarking = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, optionsMarking);
        adapterMarking.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptionsMarking.setAdapter(adapterMarking);

        ArrayAdapter<String> adapterTypeOfReference = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeOfReference);
        adapterTypeOfReference.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfReference.setAdapter(adapterTypeOfReference);

        ArrayAdapter<String> adapterProductLine = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, productLine);
        adapterProductLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductLine.setAdapter(adapterProductLine);

        ArrayAdapter<String> adapterTypeOfArticle = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeOfArticle);
        adapterTypeOfArticle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfArticle.setAdapter(adapterTypeOfArticle);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(FormsFragmentViewModel.class);

        spinnerTypeOfReference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedElement = parent.getItemAtPosition(pos).toString();
                viewModel.typeOfReference.setValue(selectedElement);

                if("A".equals(selectedElement)){
                    priceCatalogTextView.setVisibility(View.GONE);
                    editPriceCatalog.setVisibility(View.GONE);
                    radioGroupMarking.setVisibility(View.GONE);
                    viewModel.marking.setValue(false);
                } else {
                    priceCatalogTextView.setVisibility(View.VISIBLE);
                    editPriceCatalog.setVisibility(View.VISIBLE);
                    markingTextView.setVisibility(View.VISIBLE);
                    radioGroupMarking.setVisibility(View.VISIBLE);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editRequesterName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence userValue, int start, int before, int count) {
                viewModel.requesterName.setValue(userValue.toString());
            }
        });

        spinnerProductLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                viewModel.productLine.setValue(parent.getItemAtPosition(pos).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerTypeOfArticle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                 viewModel.typeOfArticle.setValue(parent.getItemAtPosition(pos).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editProductClassificationDescription.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence userValue, int start, int before, int count) {
                viewModel.productClassificationDescription.setValue(userValue.toString());
            }
        });

        editPriceCatalog.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence userValue, int start, int before, int count) {
                viewModel.priceCatalog.setValue(userValue.toString());
            }
        });
        radioGroupMarking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.button_yes) {
                    viewModel.marking.setValue(true);
                    spinnerOptionsMarking.setVisibility(View.VISIBLE);
                } else {
                    viewModel.marking.setValue(false);
                    spinnerOptionsMarking.setVisibility(View.GONE);
                }
            }


        });

        spinnerOptionsMarking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                viewModel.typeOfMarking.setValue(parent.getItemAtPosition(pos).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        myButtonSend.setOnClickListener(v -> {
            String token = null;
            try {
                token = secureAuthStore.getAccessToken();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            if (token == null || token.isEmpty()) {
                Toast.makeText(getContext(), "Invalid token", Toast.LENGTH_SHORT).show();
                return;
            }
            // get data from viewModel
            String typeOfReferenceViewModel = viewModel.typeOfReference.getValue();
            String requesterNameViewModel = viewModel.requesterName.getValue();
            String productLineViewModel = viewModel.productLine.getValue();
            String typeOfArticleViewModel = viewModel.typeOfArticle.getValue();
            String productClassificationViewModel = viewModel.productClassificationDescription.getValue();
            Boolean markingViewModel = viewModel.marking.getValue();
            String typeOfMarkingViewModel = viewModel.typeOfMarking.getValue();
            String email = null;
            try {
                email = secureAuthStore.getEmailFromToken(secureAuthStore.getAccessToken());
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            String statusID  =  "Awaiting";

            if (markingViewModel && (typeOfMarkingViewModel == null || typeOfMarkingViewModel.isEmpty())) {
                Toast.makeText(getContext(), "Please select a marking", Toast.LENGTH_SHORT).show();
                return;
            }
            float prixCatalogue = 0f;
            if (!"A".equals(typeOfReferenceViewModel)) {
                try {
                    prixCatalogue = Float.parseFloat(viewModel.priceCatalog.getValue());
                } catch (NumberFormatException | NullPointerException e) {
                    Toast.makeText(getContext(), "Invalid price catalog", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Demand demand = new Demand(
                    typeOfReferenceViewModel,
                    requesterNameViewModel,
                    productLineViewModel,
                    typeOfArticleViewModel,
                    productClassificationViewModel,
                    prixCatalogue,
                    markingViewModel,
                    typeOfMarkingViewModel,
                    email,
                    statusID
                    //id_marquage
            );

            if((typeOfReferenceViewModel.equals("B") || typeOfReferenceViewModel.equals("C")) && markingViewModel){
                Call<Demand> call = apiService.createDemand(demand);
                call.enqueue(new Callback<Demand>() {
                    @Override
                    public void onResponse(Call<Demand> call, Response<Demand> response) {
                        Demand demand = response.body();
                        assert demand != null;
                         requestID = demand.getId() ;

                        apiService.updateValidationPricing(requestID , "Awaiting").enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(getContext(), "Request sent to pricing", Toast.LENGTH_SHORT).show();


                                apiService.updateValidationAffairesReglementaires(requestID , "Awaiting" ).enqueue(new Callback<Void>() {
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
                                Toast.makeText(getContext(), "Fail to send the request sent to pricing", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Demand> call, Throwable t) {
                        Toast.makeText(getContext(), "Fail to send the request", Toast.LENGTH_SHORT).show();

                    }
                });
            } else if(("B".equals(typeOfReferenceViewModel) || "C".equals(typeOfReferenceViewModel)) && !markingViewModel){
                apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                    @Override
                    public void onResponse(Call<Demand> call, Response<Demand> response) {
                        apiService.updateValidationPricing(response.body().getId(), "Awaiting").enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(getContext(),  "Request sent to pricing" ,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(),  "Fail to send the request to pricing" ,Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Demand> call, Throwable t) {
                        Toast.makeText(getContext(),  "Fail to send the request to pricing" ,Toast.LENGTH_SHORT).show();
                    }
                });
            } else if("D".equals(typeOfReferenceViewModel) && markingViewModel){
                apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                    @Override
                    public void onResponse(Call<Demand> call, Response<Demand> response) {
                        requestID = response.body().getId();
                        apiService.updateValidationAffairesReglementaires(response.body().getId(), "Awaiting" ).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(getContext(),  "Request send to regulatory affairs" ,Toast.LENGTH_SHORT).show();
                                apiService.updateValidationAchat(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(getContext(),  "Request send to purchase" ,Toast.LENGTH_SHORT).show();
                                        apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                Toast.makeText(getContext(),  "Request send to pricing" ,Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                Toast.makeText(getContext(),  "Fail to send the request to pricing" ,Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(getContext(),  "Fail to send the request send to purchase" ,Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(),  "Fail to send the request to regulatory affairs" ,Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Demand> call, Throwable t) {
                        Toast.makeText(getContext(),  "Fail to send the request" ,Toast.LENGTH_SHORT).show();
                    }
                });
            } else if("D".equals(typeOfReferenceViewModel) && !markingViewModel){
                apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                    @Override
                    public void onResponse(Call<Demand> call, Response<Demand> response) {
                        requestID= response.body().getId();
                        apiService.updateValidationAchat(requestID ,  "Awaiting").enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(getContext(),  "Request sent to purchase" ,Toast.LENGTH_SHORT).show();
                                apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(getContext(),  "Request sent to pricing" ,Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(getContext(),  "Fail to send the request sent to pricing" ,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(),  "Fail to send the request sent to purchase" ,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Demand> call, Throwable t) {
                        Toast.makeText(getContext(),  "Fail to send the request" ,Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                apiService.createDemand(demand).enqueue(new Callback<Demand>() {
                    @Override
                    public void onResponse(Call<Demand> call, Response<Demand> response) {
                        requestID = response.body().getId();
                        apiService.updateValidationPricing(requestID, "Awaiting").enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(getContext(),  "Request sent to pricing" ,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(),  "Request sent to pricing" ,Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<Demand> call, Throwable t) {
                        Toast.makeText(getContext(),  "Fail to send the request" ,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        

        myButtonSave.setOnClickListener(v->{
            String token = null;
            try {
                token = secureAuthStore.getAccessToken();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            if (token == null || token.isEmpty()) {
                Toast.makeText(getContext(), "Invalid Token", Toast.LENGTH_SHORT).show();
                return;
            }

            String referenceType = viewModel.typeOfReference.getValue();
            String nameOfRequester = viewModel.requesterName.getValue();
            String productRange = viewModel.productLine.getValue();
            String articleType = viewModel.typeOfArticle.getValue();
            String materialDescription = viewModel.productClassificationDescription.getValue();

            Boolean regulatoryMarking = viewModel.marking.getValue();

            String typeOfRegulatoryMarking = viewModel.typeOfMarking.getValue();

            String email = null;
            try {
                email = secureAuthStore.getEmailFromToken(secureAuthStore.getAccessToken());
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

            String statusID = "Draft";
            if (regulatoryMarking && (typeOfRegulatoryMarking == null || typeOfRegulatoryMarking.isEmpty())) {
                Toast.makeText(getContext(), "Please select a marking", Toast.LENGTH_SHORT).show();
                return;
            }

            float price = 0f;
            if (!"A".equals(referenceType)) {
                try {
                    price = Float.parseFloat(viewModel.priceCatalog.getValue());
                } catch (NumberFormatException | NullPointerException e) {
                    Toast.makeText(getContext(), "Invalid price catalog", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Demand demand = new Demand(
                    referenceType,
                    nameOfRequester,
                    productRange,
                    articleType,
                    materialDescription,
                    price,
                    regulatoryMarking,
                    typeOfRegulatoryMarking,
                    email,
                    statusID
            );

            Call<Demand> call = apiService.createDemand(demand);
            call.enqueue(new Callback<Demand>() {
                @Override
                public void onResponse(Call<Demand> call, Response<Demand> response) {
                    Toast.makeText(getContext(), "request saved successfully", Toast.LENGTH_SHORT).show();
                    NavController nav = NavHostFragment.findNavController(FormsFragment.this);
                    nav.navigate(R.id.navigation_to_myRequests);

                }

                @Override
                public void onFailure(Call<Demand> call, Throwable t) {
                    if(BuildConfig.DEBUG){
                        Log.d("PUT", "PUT erreur : " + t.getMessage());
                    }
                    Toast.makeText(getContext(), "Erreur PUT : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });
        return view;
    }


    // Méthode utilitaire générique pour lancer un PATCH
    private void enqueuePatch(Call<Void> call, String label ) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (BuildConfig.DEBUG) {
                    Log.d("PATCH", label + " retour code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.e("PATCH", label + " erreur : " + t.getMessage());
                }
                Toast.makeText(getContext(), "Erreur " + label + " : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Petite classe utilitaire pour simplifier l'usage de TextWatcher
    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
    }

}
