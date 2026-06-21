package com.example.forms.onglets.edit;
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

public class EditFragment extends Fragment {
    private SecureAuthStore secureAuthStore;
    private ApiService apiService;

    private EditViewModel editViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        editViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        //charging secureAuthstore
        try {
            secureAuthStore = new SecureAuthStore(requireContext());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // prepare for ApiRequest
        apiService = RetrofitClient.getApiService(secureAuthStore);

        //charging the view to display it to the user
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        //find the ressource spinner (dropdown) typeOfReference in fragment_edit.xml
        Spinner editTypeOfReference = view.findViewById(R.id.edit_spinner_type_of_reference);

        //find the ressource edit in fragment_edit.xml (the dedicated field to write user name)
        EditText editRequesterName = view.findViewById(R.id.edit_requester_name);

        //find the ressource spinner (dropdown) productLine in fragment_edit.xml
        Spinner editProductLine = view.findViewById(R.id.edit_spinner_productLine);

        //find the ressource spinner (dropdown) typeOfArticle in fragment_edit.xml
        Spinner spinnerTypeOfArticle = view.findViewById(R.id.edit_spinner_type_of_article);

        //find the ressource EditText for price catalog in fragment_edit.xml
        EditText editPriceCatalog  = view.findViewById(R.id.edit_PriceCatalog);

        TextView editPriceCatalogTextView = view.findViewById(R.id.edit_price_catalog_text_view);

        // find the ressource EditText for product classification description in fragment_edit.xml
        EditText editProductClassificationDescription = view.findViewById(R.id.edit_product_classification);

        //find the button ok in Fragment_edit.xml
        Button myButtonOk = view.findViewById(R.id.button_ok);

        //find the ressource Spinner for the list of marking
        Spinner dropDownMarking = view.findViewById(R.id.edit_options_marking);

        //find the ressource radiogroup in fragment_xml
        RadioGroup editRadioGroupMarking = view.findViewById(R.id.edit_radioGroup_Marking);

        TextView markingTextView =  view.findViewById(R.id.edit_text_marking);

        assert getArguments() != null;
        Demand demand = (Demand) getArguments().getSerializable("demand");

        String[] typeOfMarking = {"IVDD", "IVDR", "MDD" , "MDR", "CE", "FDA" , "IKCA"};
        String[] typeOfReference = {"A", "B", "C" , "D", "E", "F" , "G", "H"};
        String[] productLine = {"Gamme 1", "Gamme 2", "Gamme 3"};
        String[] typeOfArticle = {"Type X", "Type Y", "Type Z"};

        assert demand != null;
        if(demand.isMarking()){
            editRadioGroupMarking.check(R.id.edit_button_yes);
            dropDownMarking.setVisibility(View.VISIBLE);
        } else {
            editRadioGroupMarking.check(R.id.edit_button_no);
        }
        editRadioGroupMarking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.edit_button_yes) {
                    editViewModel.marking.setValue(true);
                    dropDownMarking.setVisibility(View.VISIBLE);
                } else {
                    editViewModel.marking.setValue(false);
                    dropDownMarking.setVisibility(View.GONE);
                }
            }
        });


        ArrayAdapter<String> adapterTypeOfMarking = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item , typeOfMarking);
        adapterTypeOfMarking.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownMarking.setAdapter(adapterTypeOfMarking);

        int indexOfUserValueForTypeOfMarking = adapterTypeOfMarking.getPosition(demand.getTypeOfMarking());
        if(indexOfUserValueForTypeOfMarking >=0){
            System.out.println("USER SELECTED " + demand.getTypeOfMarking());
            dropDownMarking.setSelection(indexOfUserValueForTypeOfMarking);
        }

        dropDownMarking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                Log.d("TEST", "onItemSelected déclenché, valeur = " + selected);
                editViewModel.typeOfMarking.setValue(selected);
                Log.d("TEST", "editViewModel value after, valeur = " + editViewModel.typeOfMarking.getValue());


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });



        //create an adapter that make a link between our data list and the Spinner.
        ArrayAdapter<String> adapterTypeOfReference = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeOfReference);
        adapterTypeOfReference.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTypeOfReference.setAdapter(adapterTypeOfReference);

        if("Awaiting".equals(demand.getStatusID())){
            editTypeOfReference.setEnabled(false);
            editTypeOfReference.setAlpha(0.5f);
        } else {
            editTypeOfReference.setEnabled(true);
            editTypeOfReference.setAlpha(1.0f);
        }
        int indexOfUserValueForTypeOfReference = adapterTypeOfReference.getPosition(demand.getTypeOfReference());
        if(indexOfUserValueForTypeOfReference >=0){
            editTypeOfReference.setSelection(indexOfUserValueForTypeOfReference);
        }

        editTypeOfReference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedElement = parent.getItemAtPosition(pos).toString();
                editViewModel.typeOfReference.setValue(selectedElement);

                if("A".equals(selectedElement)
                        || "E".equals(selectedElement)
                        || "F".equals(selectedElement)
                        || "G".equals(selectedElement)
                        || "H".equals(selectedElement)){
                    editPriceCatalog.setVisibility(View.GONE);
                    editPriceCatalogTextView.setVisibility(View.GONE);
                    editRadioGroupMarking.setVisibility(View.GONE);
                    markingTextView.setVisibility(View.GONE);
                    editViewModel.marking.setValue(false);
                } else {
                    editPriceCatalog.setVisibility(View.VISIBLE);
                    editPriceCatalogTextView.setVisibility(View.VISIBLE);
                    markingTextView.setVisibility(View.VISIBLE);
                    editRadioGroupMarking.setVisibility(View.VISIBLE);
                    markingTextView.setVisibility(View.VISIBLE);

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterProductLine = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, productLine);
        adapterProductLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editProductLine.setAdapter(adapterProductLine);

        int indexOfUserValueForProductLine = adapterProductLine.getPosition(demand.getProductLine());
        if(indexOfUserValueForProductLine >=0){
            editProductLine.setSelection(indexOfUserValueForProductLine);
        }
        editProductLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                editViewModel.productLine.setValue(parent.getItemAtPosition(pos).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterTypeOfArticle = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeOfArticle);
        adapterTypeOfArticle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfArticle.setAdapter(adapterTypeOfArticle);

        spinnerTypeOfArticle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                editViewModel.typeOfArticle.setValue(parent.getItemAtPosition(pos).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        editRequesterName.setText(demand.getRequesterName());
        editRequesterName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence userValue, int start, int before, int count) {
                editViewModel.requesterName.setValue(userValue.toString());
            }
        });


        editProductClassificationDescription.setText(demand.getProductClassificationDescription());
        editProductClassificationDescription.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence userValue, int start, int before, int count) {
               editViewModel.productClassificationDescription.setValue(userValue.toString());
            }
        });

        editPriceCatalog.setText(Float.toString(demand.getPriceCatalog()) );
        editPriceCatalog.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence userValue, int start, int before, int count) {
              editViewModel.priceCatalog.setValue(userValue.toString());
            }

        });

        myButtonOk.setOnClickListener(v -> {
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

            String newTypeOfReference = editViewModel.typeOfReference.getValue() != null ? editViewModel.typeOfReference.getValue(): demand.getTypeOfReference() ;
            String newRequesterName = editViewModel.requesterName.getValue() != null ? editViewModel.requesterName.getValue() : demand.getRequesterName();
            String newProductLine = editViewModel.productLine.getValue() != null ? editViewModel.productLine.getValue() : demand.getProductLine();
            String newTypeOfArticle = editViewModel.typeOfArticle.getValue() ;
            String newProductClassification = editViewModel.productClassificationDescription.getValue() != null ? editViewModel.productClassificationDescription.getValue() : demand.getProductClassificationDescription();
            boolean newMarking = editViewModel.marking.getValue() != null ? editViewModel.marking.getValue(): demand.isMarking();
            String newTypeOfMarking = editViewModel.typeOfMarking.getValue() != null ? editViewModel.typeOfMarking.getValue() : demand.getTypeOfMarking() ;


            if (newMarking && (newTypeOfMarking == null || newTypeOfMarking.isEmpty())) {
                Toast.makeText(getContext(), "Please select a marking", Toast.LENGTH_SHORT).show();
                return;
            }
            float priceCatalog = 0f;
            if (!"A".equals(newTypeOfReference)) {
                try {
                    priceCatalog = Float.parseFloat(editViewModel.priceCatalog.getValue() != null ? editViewModel.priceCatalog.getValue() : String.valueOf(demand.getPriceCatalog()));
                } catch (NumberFormatException | NullPointerException e) {
                    Toast.makeText(getContext(), "Invalid price catalog", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            demand.setTypeOfReference(newTypeOfReference);
            demand.setRequesterName(newRequesterName);
            demand.setProductLine(newProductLine);
            demand.setTypeOfArticle(newTypeOfArticle);
            demand.setProductClassificationDescription(newProductClassification);
            demand.setPriceCatalog(priceCatalog);
            demand.setMarking(newMarking);
            demand.setTypeOfMarking(newTypeOfMarking);

            apiService.editUserRequest(demand.getId(), demand).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(getContext(), "Request update successfully", Toast.LENGTH_SHORT).show();
                    NavController nav = NavHostFragment.findNavController(EditFragment.this);
                    nav.navigate(R.id.navigation_to_myRequests);
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Fail to update the request successfully", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return view ;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
    }
}
