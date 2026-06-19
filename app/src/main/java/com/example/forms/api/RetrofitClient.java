package com.example.forms.api;
import com.example.forms.security.AuthInterceptor;
import com.example.forms.security.SecureAuthStore;
import com.example.forms.security.TokenRefreshHandler;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {
    private static final String BASE_URL = "http://X.X.X.X:8080/";
    private static ApiService apiService;

    public static ApiService getApiService(SecureAuthStore authStore) {
        if (apiService == null) {
            AuthInterceptor authInterceptor = new AuthInterceptor(authStore);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();
            apiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
            AuthService authService = RetrofitClientForRefresh.getAuthService(authStore);
            TokenRefreshHandler handler = new TokenRefreshHandler(authService, authStore);
            authInterceptor.setTokenRefreshHandler(handler);
        }
        return apiService;
    }
}
