package io.github.coffeegerm.materiallogbook.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/*
* Activity for controlling which fragment should be presented and containing
* the main activity for holding Fragments
* */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment listFragment = new ListFragment();
    Fragment graphFragment = new GraphFragment();
    Fragment settingsFragment = new SettingsFragment();
    Fragment newsFragment = new NewsFragment();
    Fragment statsFragment = new StatisticsFragment();
    FragmentManager fragmentManager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        realmSetup();
        setDrawerLayout();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, listFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            // Swaps fragment to list fragment
            fragmentManager.beginTransaction().replace(R.id.fragment_container, listFragment).commit();
        } else if (id == R.id.nav_graph) {
            // Swaps fragment to graph fragment
            fragmentManager.beginTransaction().replace(R.id.fragment_container, graphFragment).commit();
        } else if (id == R.id.nav_stats) {
            // Swaps fragment to statistics fragment
            fragmentManager.beginTransaction().replace(R.id.fragment_container, statsFragment).commit();
        } else if (id == R.id.nav_settings) {
            // Swaps fragment to settings fragment
            fragmentManager.beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
        } else if (id == R.id.nav_news) {
            //Swaps fragment to news fragment
            fragmentManager.beginTransaction().replace(R.id.fragment_container, newsFragment).commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void realmSetup() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);
    }

    private void setDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }
}
