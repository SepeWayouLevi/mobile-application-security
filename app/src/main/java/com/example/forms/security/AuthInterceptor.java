package com.example.forms.security;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.forms.BuildConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class AuthInterceptor implements Interceptor {
    private final SecureAuthStore authStore ;

    private  TokenRefreshHandler tokenRefreshHandler;


    public AuthInterceptor(SecureAuthStore authStore) {
        this.authStore = authStore;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String accessToken = null;
        try {
            accessToken = authStore.getAccessToken();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        String refreshToken = null;
        try {
            refreshToken = authStore.getRefreshToken();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        if(BuildConfig.DEBUG){
            Log.d("AUTH_INTERCEPTOR", "accessToken null ? " + (accessToken == null));
            Log.d("AUTH_INTERCEPTOR", "tokenIsAboutToExpired ? " + authStore.tokenIsAboutToExpire(accessToken));
            Log.d("AUTH_INTERCEPTOR", "refreshToken null ? " + (refreshToken == null));
            Log.d("AUTH_INTERCEPTOR", "refreshToken expired ? " + authStore.isTokenExpired(refreshToken));
        }


        if ((accessToken == null || authStore.tokenIsAboutToExpire(accessToken))
                && (!authStore.isTokenExpired(refreshToken) && refreshToken != null)){
            try {
                return handleRefresh(chain, original);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
        else if (refreshToken == null || authStore.isTokenExpired(refreshToken)){
            Log.d("AUTH_INTERCEPTOR", ">>> notifyUnauthorized triggered");
            AuthEventBus.getInstance().notifyUnauthorized();
            return  buildUnauthorizedResponse(original);
        }
        Log.d("AUTH_INTERCEPTOR", ">>> normal request with accessToken");
        Request requestWithToken = buildRequestWithToken(original, accessToken);
        return chain.proceed(requestWithToken);
    }

    private Response handleRefresh(Chain chain, Request original) throws IOException, GeneralSecurityException {
        Boolean newTokens = tokenRefreshHandler.execute();

        if(newTokens){
            Log.d("Handle refresh", ">>> Next request with accessToken" + true);

            Request myRequest = buildRequestWithToken(original, authStore.getAccessToken());
           return chain.proceed(myRequest);

        } else {
            return buildUnauthorizedResponse(original);
        }
    }
    private Request buildRequestWithToken(Request original, String token) {
        return original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    private Response buildUnauthorizedResponse(Request request) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Session expired, please log in again")
                .body(ResponseBody.create("", MediaType.parse("application/json")))
                .build();
    }


    public void setTokenRefreshHandler(TokenRefreshHandler tokenRefreshHandler) {
        this.tokenRefreshHandler = tokenRefreshHandler;
    }
}