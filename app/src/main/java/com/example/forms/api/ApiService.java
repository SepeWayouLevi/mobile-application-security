package com.example.forms.api;
import com.example.forms.models.Demand;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/forms/byCountry")
    Call<List<Demand>> getDemandsByCountry(
            @Header("User-Email") String email
    );
    @POST("/api/forms/createUserForms")
    Call<Demand> createDemand(@Body Demand demand);

    @PATCH("/api/forms/{id_demand}/pricingValidation")
    Call<Void> updateValidationPricing(@Path("id_demand") Long id, @Body String validation);

    @PATCH("/api/forms/{idDemand}/regulatoryAffairsValidation")
    Call<Void> updateValidationAffairesReglementaires(@Path("idDemand") Long id, @Body String validation);

    @PATCH("/api/forms/{idDemand}/purchaseValidation")
    Call<Void> updateValidationAchat(@Path("idDemand") Long id, @Body String validation);

    @PUT("/api/forms/{idDemand}/editRequest")
    Call<Void> editUserRequest(@Path("idDemand") Long id, @Body Demand demand);

    @GET("/api/forms/WithRefD")
    Call<List<Demand>> getFormulaireWithRefD(
            @Header("User-Email") String email,
            @Query("typeofreference") String typeDeReference
    );

    @GET("/api/forms/withMarking")
    Call<List<Demand>> getFormulaireWithMarquage(
            @Header("User-Email") String email
    );

    @GET("/api/forms/Pricing")
    Call<List<Demand>> getFormulaireWithAPrice(
            @Header("User-Email") String email
    );

    @GET("/api/forms/userForms")
    Call<List<Demand>> getFormulaireByEmail(
            @Header("User-Email") String email
    );



}
