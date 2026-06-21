package com.example.forms.security;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;


import com.example.forms.BuildConfig;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.PredefinedAeadParameters;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;


public class SecureAuthStore {
    private static final String KEYSET_ALIAS = "auth_keyset";
    private static final String KEYSET_PREFS = "auth_keyset_prefs";
    private static final String PREFS_NAME   = "auth_prefs";
    private static final String KEY_ACCESS   = "access_token";
    private static final String KEY_REFRESH  = "refresh_token";

    private static final String ASSOCIATED_DATA  =  "secure_apps";
    private final SharedPreferences prefs;
    private final Aead aead;

    public SecureAuthStore(Context context) throws GeneralSecurityException, IOException {
        AeadConfig.register();
        KeysetHandle keysetHandle = new AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_ALIAS, KEYSET_PREFS)
                .withKeyTemplate(KeyTemplate.createFrom(PredefinedAeadParameters.AES256_GCM))
                .withMasterKeyUri("android-keystore://auth_master_key")
                .build()
                .getKeysetHandle();
        this.aead  = keysetHandle.getPrimitive(Aead.class);
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    /**
     * Encrypts a token and saves it in SharedPreferences.
     * @param key   KEY_ACCESS or KEY_REFRESH
     * @param token clear text token value
     */
    private void encryptToken(String key, String token) throws GeneralSecurityException {
        byte[] ciphertext = aead.encrypt(
                token.getBytes(StandardCharsets.UTF_8),
                ASSOCIATED_DATA.getBytes(StandardCharsets.UTF_8)
        );
        String encoded = Base64.encodeToString(ciphertext, Base64.NO_WRAP);
        prefs.edit().putString(key, encoded).apply();
    }

    /**
     * Reads and decrypts a token from SharedPreferences.
     * @param key   KEY_ACCESS or KEY_REFRESH
     * @return the clear text token, or null if absent
     */
    private String decryptToken(String key) throws GeneralSecurityException {
        String encoded = prefs.getString(key, null);
        if (encoded == null) return null;

        byte[] ciphertext = Base64.decode(encoded, Base64.NO_WRAP);
        byte[] plaintext  = aead.decrypt(ciphertext, ASSOCIATED_DATA.getBytes(StandardCharsets.UTF_8));
        return new String(plaintext, StandardCharsets.UTF_8);
    }


    public void setAccessToken(String accessToken) throws GeneralSecurityException{
        encryptToken(KEY_ACCESS, accessToken);

    }
    public void setRefreshToken(String token) throws GeneralSecurityException {
        encryptToken(KEY_REFRESH, token);
    }

    public String getAccessToken() throws GeneralSecurityException {
        return decryptToken(KEY_ACCESS);
    }
    public String getRefreshToken() throws GeneralSecurityException{
        return decryptToken(KEY_REFRESH);
    }
    public void clearAccessToken(){
        prefs.edit().remove(KEY_ACCESS).apply();
    }
    public void clearRefreshToken(){
        prefs.edit().remove(KEY_REFRESH).apply();
    }


    public boolean isTokenExpired(String theToken) {
        boolean isExpired =  false;
        try {
            String[] parts = theToken.split("\\.");
            String payloadJson = new String(
                    android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
            );
            JSONObject payload = new JSONObject(payloadJson);

            long exp = payload.getLong("exp");
            long now = System.currentTimeMillis() / 1000L;

            if(now >= exp){
                isExpired =  true;
                return isExpired;
            }

        } catch (Exception e) {
            isExpired = true;
            return isExpired;
        }
        return isExpired;
    }

    public boolean tokenIsAboutToExpire(String theToken){
        boolean isAboutToExpired = false ;
        try {
            String[] parts = theToken.split("\\.");
            String payloadOfTheToken  = new String(
                    android.util.Base64.decode(parts[1],  Base64.URL_SAFE)
            );
            JSONObject payloadInTheCorrectFormat  =  new JSONObject(payloadOfTheToken);
            long  tokenExpiration = payloadInTheCorrectFormat.getLong("exp") ;
            float tokenExpirationInMinutes = (tokenExpiration - (System.currentTimeMillis() / 1000))/ 60f ;

            if(BuildConfig.DEBUG){
                System.out.println(">>>> TOKEN EXPIRE IN " + tokenExpirationInMinutes + "MINUTES");
            }

            if(tokenExpirationInMinutes <= 1){
                isAboutToExpired =  true;
                return isAboutToExpired;
            }
        } catch (Exception e){
            isAboutToExpired =  true;
            return isAboutToExpired;
        }
        return isAboutToExpired  ;
    }

    private static String decodeBase64UrlToString(String base64Url) {
        int rem = base64Url.length() % 4;
        if (rem == 2)      base64Url += "==";
        else if (rem == 3) base64Url += "=";

        byte[] bytes = Base64.decode(base64Url, Base64.URL_SAFE | Base64.NO_WRAP);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    public String getEmailFromToken(String theToken) {
        if (theToken == null || theToken.isEmpty()) return null;
        try {
            String[] parts = theToken.split("\\.");
            String payloadJson = decodeBase64UrlToString(parts[1]);
            JSONObject payload = new JSONObject(payloadJson);
            return payload.optString("sub", null);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Token invalide");
            }
            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject payload = new JSONObject(payloadJson);
            JSONArray rolesArray = payload.getJSONArray("roles");
            if (rolesArray.length() > 0) {
                JSONObject roleObject = rolesArray.getJSONObject(0);
                return roleObject.getString("authority");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
