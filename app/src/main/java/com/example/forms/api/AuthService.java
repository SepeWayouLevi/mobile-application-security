package com.example.forms.api;
import com.example.forms.onglets.login.LoginInformations;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthService {
    @GET("/api/authentication/accessToken")
    Call<Map<String, String>> getAccessToken();

    @POST("/api/authentication/signin")
    Call<ResponseBody> signUser(@Body LoginInformations loginInformations);

    @POST("/api/authentication/users/disconnect")
    Call<Void> logout(@Header("Refresh-Token") String refreshToken);



}
