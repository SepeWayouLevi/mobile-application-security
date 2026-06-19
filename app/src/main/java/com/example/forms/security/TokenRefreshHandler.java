package com.example.forms.security;
import android.util.Log;

import com.example.forms.api.AuthService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
public class TokenRefreshHandler {
    private final AuthService authService;
    private final SecureAuthStore authStore;

    public TokenRefreshHandler(AuthService authService, SecureAuthStore authStore) {
        this.authService = authService;
        this.authStore = authStore;
    }
    public Boolean execute() throws IOException, GeneralSecurityException {
        boolean isSuccess  =  false;
        System.out.println("testing something " + !isSuccess);
        Log.d("IN TOKEN REFRESH HANDLER", ">>> TRYING TO EXECUTE "  ) ;

        retrofit2.Response<Map<String,String>> refreshResponse = authService
                .getAccessToken()
                .execute();
        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
            Log.d("From token RefreshHandler response is successful", ">>> Saving the new access token"  ) ;
            Map<String,String> body = refreshResponse.body();
            Log.d("From token RefreshHandler", ">>> following request with an accessToken" + body);
            String newAccessToken  = body.get("access_token") ;

            authStore.setAccessToken(newAccessToken);
            return !isSuccess;
        }
        else {
            AuthEventBus.getInstance().notifyUnauthorized();
            return isSuccess;
        }
    }

}