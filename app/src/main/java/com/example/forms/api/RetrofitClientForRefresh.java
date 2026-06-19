package com.example.forms.api;

import com.example.forms.security.AuthInterceptorRefresh;
import com.example.forms.security.SecureAuthStore;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitClientForRefresh {
    private static final String BASE_URL = "http://x.x.x.x:8080/";;
    private static AuthService authService;
    public static AuthService getAuthService(SecureAuthStore authStore) {
        if(authService == null){
            AuthInterceptorRefresh authInterceptorRefresh = new AuthInterceptorRefresh(authStore);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptorRefresh)
                    .build();
            authService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AuthService.class);
        }
        return authService;
    }
}
