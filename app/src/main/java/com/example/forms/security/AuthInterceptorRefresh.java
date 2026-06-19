package com.example.forms.security;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptorRefresh implements Interceptor {
    private final SecureAuthStore authStore ;
    public AuthInterceptorRefresh(SecureAuthStore authStore) {
        this.authStore = authStore;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        if(original.url().encodedPath().equals("/api/authentication/users/disconnect")){
            String accessToken = null;
            try {
                accessToken = authStore.getAccessToken();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            Request requestWithAccessToken = buildRequestWithToken(original, accessToken);
            return chain.proceed(requestWithAccessToken);

        } else {
            String refreshToken = null;
            try {
                refreshToken = authStore.getRefreshToken();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            Request requestWithToken = buildRequestWithToken(original, refreshToken);
            return chain.proceed(requestWithToken);

        }


    }
    private Request buildRequestWithToken(Request original, String token) {
        return original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
    }

}
