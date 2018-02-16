/*
 * Copyright 2018 Coffee and Cream Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.coffeegerm.glucoseguide.ui;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.glucoseguide.R;
import io.github.coffeegerm.glucoseguide.ui.list.ListFragment;
import io.github.coffeegerm.glucoseguide.ui.settings.SettingsActivity;
import io.github.coffeegerm.glucoseguide.ui.statistics.StatisticsFragment;
import io.github.coffeegerm.glucoseguide.ui.support.SupportFragment;
import io.github.coffeegerm.glucoseguide.utils.Utilities;

import static io.github.coffeegerm.glucoseguide.GlucoseGuide.syringe;
import static io.github.coffeegerm.glucoseguide.utils.Constants.PREF_DARK_MODE;

public class MainActivity extends AppCompatActivity
      implements NavigationView.OnNavigationItemSelectedListener {
  
  @Inject
  public SharedPreferences sharedPreferences;
  @Inject
  public Utilities utilities;
  
  public static boolean isResumed = false;
  private boolean isCreated = false;
  public int lastSelectedTab;
  
  Fragment listFragment = new ListFragment();
  Fragment statsFragment = new StatisticsFragment();
  Fragment supportFragment = new SupportFragment();
  FragmentManager fragmentManager;
  
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.nav_view)
  NavigationView navigationView;
  @BindView(R.id.feedback)
  TextView feedback;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    syringe.inject(this);
    if (sharedPreferences.getBoolean(PREF_DARK_MODE, false)) setTheme(R.style.AppTheme_Dark);
    setContentView(R.layout.activity_main);
    isCreated = true;
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
    setDrawerLayout();
    setNavigationView();
    fragmentManager = getSupportFragmentManager();
    if (isCreated && !isResumed) setFragment(listFragment);
    feedback.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendFeedbackEmail();
      }
    });
    setGlucoseGrade();
  }
  
  @Override
  protected void onResume() {
    isResumed = true;
    if (sharedPreferences.getBoolean(PREF_DARK_MODE, false))
      setTheme(R.style.AppTheme_Dark);
    else setTheme(R.style.AppTheme);
    
    if (isCreated) isCreated = false;
    else {
      recreate();
      navigationView.setCheckedItem(lastSelectedTab);
      switch (lastSelectedTab) {
        case R.id.nav_list:
          setFragment(listFragment);
          break;
        case R.id.nav_stats:
          setFragment(statsFragment);
          break;
      }
    }
    
    super.onResume();
  }
  
  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START))
      drawerLayout.closeDrawer(GravityCompat.START);
    else
      new AlertDialog.Builder(this).setTitle(R.string.close_app)
            .setMessage(R.string.close_confirmation)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                finish();
              }
            }).setNegativeButton(R.string.no, null).show();
  }
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nav_list:
        setFragment(listFragment);
        lastSelectedTab = R.id.nav_list;
        break;
      
      case R.id.nav_stats:
        setFragment(statsFragment);
        lastSelectedTab = R.id.nav_stats;
        break;
      
      case R.id.nav_support:
        setFragment(supportFragment);
        lastSelectedTab = R.id.nav_support;
        break;
      
      case R.id.nav_settings:
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        navigationView.setCheckedItem(lastSelectedTab);
        break;
    }
    
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }
  
  private void setFragment(Fragment fragment) {
    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
  }
  
  private void setDrawerLayout() {
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
          toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();
    navigationView.setNavigationItemSelectedListener(this);
    // Navigation Drawer should be 80% of screen width when open
    DisplayMetrics displayMetrics = MainActivity.this.getResources().getDisplayMetrics();
    int fullWidth = displayMetrics.widthPixels;
    int navViewWidth = Math.round(0.8f * fullWidth);
    ViewGroup.LayoutParams sidebarParameters = navigationView.getLayoutParams();
    sidebarParameters.width = navViewWidth;
  }
  
  private void setNavigationView() {
    int textColor;
    if (sharedPreferences.getBoolean(PREF_DARK_MODE, false)) {
      navigationView.getHeaderView(0).setBackground(getResources()
            .getDrawable(R.drawable.header_dark));
      navigationView.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
      textColor = R.color.textColorPrimaryInverse;
    } else {
      navigationView.getHeaderView(0).setBackground(getResources()
            .getDrawable(R.drawable.header_light));
      textColor = R.color.textColorPrimary;
    }
    
    ColorStateList csl = new ColorStateList(
          new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
          new int[]{getResources().getColor(R.color.colorPrimary), getResources().getColor(textColor)});
    navigationView.setItemTextColor(csl);
    navigationView.setItemIconTintList(csl);
  }
  
  // Set grade in Navigation Menu
  private void setGlucoseGrade() {
    View header = navigationView.getHeaderView(0);
    TextView glucoseGrade = header.findViewById(R.id.navigation_view_grade);
    glucoseGrade.setText(utilities.getGlucoseGrade());
  }
  
  private void sendFeedbackEmail() {
    String mailto = "mailto:coffeeandcreamstudios@gmail.com";
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
    emailIntent.setData(Uri.parse(mailto));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Material Logbook feedback");
    try {
      startActivity(emailIntent);
    } catch (ActivityNotFoundException e) {
      Toast.makeText(this, "Unable to send email, please try again.", Toast.LENGTH_SHORT).show();
    }
  }
}