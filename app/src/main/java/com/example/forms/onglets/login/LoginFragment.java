package com.example.forms.onglets.login;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.text.TextWatcher;
import android.widget.Toast;
import com.example.forms.R;
import com.example.forms.api.AuthService;
import com.example.forms.security.SecureAuthStore;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private SecureAuthStore authStore;

    private static final String BASE_URL= "http://X.X.X.X:8080/";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            authStore = new SecureAuthStore(requireContext());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initializeViews(view);
        setupTextWatchers(view);
        setupLoginButton(view);
        return view;
    }

    private void initializeViews(View view) {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupTextWatchers(View view) {
        setupTextWatcher(view, R.id.UserEmail, text -> loginViewModel.email.setValue(text));
        setupTextWatcher(view, R.id.UserPassword, text -> loginViewModel.password.setValue(text));

    }

    private void setupTextWatcher(View view, int viewId, Consumer<String> onTextChanged) {
        EditText editText = view.findViewById(viewId);
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextChanged.accept(s.toString());
            }
        });
    }

    private void setupLoginButton(View view) {
        Button signInButton = view.findViewById(R.id.buttonLogin);
        signInButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        if (!validateFields()) return;

        LoginInformations loginInformations = createLoginInformations();
        Retrofit retrofit = createRetrofitInstance();
        AuthService authService = retrofit.create(AuthService.class);

        Call<ResponseBody> call = authService.signUser(loginInformations);
        call.enqueue(new LoginCallback());
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(loginViewModel.email.getValue()) ||
                TextUtils.isEmpty(loginViewModel.password.getValue())) {
            Toast.makeText(getContext(), "Email and password are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private LoginInformations createLoginInformations() {
        return new LoginInformations(
                loginViewModel.email.getValue(),
                loginViewModel.password.getValue()
        );
    }

    private Retrofit createRetrofitInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    private class LoginCallback implements Callback<ResponseBody> {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful() && response.body() != null) {
                handleSuccessResponse(response);
            } else {
                handleErrorResponse(response);
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(getContext(), "Connection failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("API Error", "Connection failure", t);
        }

        private void handleSuccessResponse(Response<ResponseBody> response) {
            try {

                if (response.body() == null) {
                    handleTokenReadError(new IOException("Response body is null"));
                    return;
                }

                String theResponse =  response.body().string();
                JSONObject jsonFormat  =  new JSONObject(theResponse);

                String accessToken =  jsonFormat.getString("access_token");
                String refreshToken =  jsonFormat.getString("refresh_token");

                authStore.setAccessToken(accessToken);
                authStore.setRefreshToken(refreshToken);



                navigateToAllDemands();
            } catch (IOException e) {
                handleTokenReadError(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleErrorResponse(Response<ResponseBody> response) {
            try {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "Unknown error";
                Toast.makeText(getContext(), "Error: " + response.code() + " - " + errorBody,
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Connection failure", Toast.LENGTH_SHORT).show();
            }
        }

        private void handleTokenReadError(IOException e) {
            Toast.makeText(getContext(), "rror reading token", Toast.LENGTH_SHORT).show();
            Log.e("API Error", "Error reading token", e);
        }

        private void navigateToAllDemands() {
            NavController navController = NavHostFragment.findNavController(LoginFragment.this);
            navController.navigate(R.id.navigation_to_AllDemands);
        }
    }

    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
