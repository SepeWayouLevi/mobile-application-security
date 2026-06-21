package com.example.forms;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.forms.api.AuthService;
import com.example.forms.databinding.ActivityMainBinding;
import com.example.forms.security.AuthEventBus;
import com.example.forms.api.RetrofitClientForRefresh;
import com.example.forms.security.SecureAuthStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.IOException;
import java.security.GeneralSecurityException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private AuthService authService;
    private SecureAuthStore secureAuthStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        try {
            secureAuthStore = new SecureAuthStore(getApplicationContext());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        authService = RetrofitClientForRefresh.getAuthService(secureAuthStore);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_to_AllDemands,
                R.id.navigation_to_Achats,
                R.id.navigation_to_affaires_reglementaires,
                R.id.navigation_to_pricing,
                R.id.navigation_to_inventory_planning ,
                R.id.navigation_to_myRequests,
                R.id.navigation_to_listofProducts,
                R.id.navigation_to_infos
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();
        final NavController navController =
                Navigation.findNavController(this, R.id.nav_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        NavigationUI.setupWithNavController(binding.navDrawerView, navController);


        AuthEventBus.getInstance().getUnauthorizedEvent().observe(this, unauthorized -> {
            if (Boolean.TRUE.equals(unauthorized)) {
                navController.navigate(R.id.loginPage);
                secureAuthStore.clearRefreshToken();
                secureAuthStore.clearAccessToken();
            }
        });
        binding.navDrawerView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_logout) {
                binding.drawerLayout.closeDrawers();
                navController.navigate(
                        R.id.loginPage,
                        null,
                        new androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.mobile_navigation, true)
                                .build()
                );

                String refreshToken = null;
                try {
                    refreshToken = secureAuthStore.getRefreshToken();
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
                authService.logout(refreshToken).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        secureAuthStore.clearRefreshToken();
                        secureAuthStore.clearAccessToken();
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        secureAuthStore.clearRefreshToken();
                        secureAuthStore.clearAccessToken();

                    }
                });
                // 2) Fermer le tiroir

                return true; // déjà géré
            }

            // Sinon, laisser NavigationUI gérer la navigation normale
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

            if (handled) {
                // Optionnel: cocher/décocher + fermer le tiroir
                item.setChecked(true);
                binding.drawerLayout.closeDrawers();
            }
            return handled;
        });


        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            BottomNavigationView bottomNav = binding.bottomNavView;
            boolean onLogin = destination.getId() == R.id.loginPage;
            if (onLogin) {
                bottomNav.setVisibility(android.view.View.GONE);
                binding.boutonPlus.setVisibility(android.view.View.GONE);
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                binding.toolbar.setNavigationIcon(null);

            } else  {
                String theRoleFromToken = null;
                try {
                    theRoleFromToken = secureAuthStore.getRoleFromToken(secureAuthStore.getAccessToken());
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
                switch (theRoleFromToken) {
                    case "ROLE_ADMIN":
                        bottomNav.setVisibility(View.VISIBLE);
                        binding.boutonPlus.setVisibility(View.VISIBLE);
                        bottomNav.getMenu().findItem(R.id.navigation_to_AllDemands).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_inventory_planning).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_affaires_reglementaires).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_Achats).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_pricing).setVisible(true);
                        binding.navDrawerView.getMenu().findItem(R.id.navigation_to_myRequests).setVisible(true);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        break;
                    case "ROLE_PRICING":
                        bottomNav.getMenu().findItem(R.id.navigation_to_inventory_planning).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_affaires_reglementaires).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_Achats).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_pricing).setVisible(true);
                        binding.navDrawerView.getMenu().findItem(R.id.navigation_to_myRequests).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_AllDemands).setVisible(true);
                        bottomNav.setVisibility(View.VISIBLE);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        break;
                    case "ROLE_LAW":
                        bottomNav.getMenu().findItem(R.id.navigation_to_inventory_planning).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_affaires_reglementaires).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_Achats).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_pricing).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_AllDemands).setVisible(true);
                        binding.navDrawerView.getMenu().findItem(R.id.navigation_to_myRequests).setVisible(false);
                        bottomNav.setVisibility(View.VISIBLE);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        break;
                    case "ROLE_PURCHASE":
                        bottomNav.getMenu().findItem(R.id.navigation_to_inventory_planning).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_affaires_reglementaires).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_Achats).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_pricing).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_AllDemands).setVisible(true);
                        binding.navDrawerView.getMenu().findItem(R.id.navigation_to_myRequests).setVisible(false);
                        bottomNav.setVisibility(View.VISIBLE);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        break;
                    case "ROLE_STOCK":
                        bottomNav.getMenu().findItem(R.id.navigation_to_inventory_planning).setVisible(true);
                        bottomNav.getMenu().findItem(R.id.navigation_to_affaires_reglementaires).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_Achats).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_pricing).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_AllDemands).setVisible(true);
                        binding.navDrawerView.getMenu().findItem(R.id.navigation_to_myRequests).setVisible(false);
                        bottomNav.setVisibility(View.VISIBLE);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        break;
                    case "ROLE_GENERAL":
                        bottomNav.getMenu().findItem(R.id.navigation_to_inventory_planning).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_affaires_reglementaires).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_Achats).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_pricing).setVisible(false);
                        bottomNav.getMenu().findItem(R.id.navigation_to_AllDemands).setVisible(true);
                        binding.navDrawerView.getMenu().findItem(R.id.navigation_to_myRequests).setVisible(true);
                        bottomNav.setVisibility(View.VISIBLE);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        binding.boutonPlus.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        binding.boutonPlus.setOnClickListener(v ->
                navController.navigate(R.id.formulaireFragment)
        );





    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }




}