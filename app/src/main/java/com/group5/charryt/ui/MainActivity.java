package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.group5.charryt.R;
import com.group5.charryt.Utils;

public class MainActivity extends AppCompatActivity {

    // UI components
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Menu navigationMenu;
    private Toolbar toolbar;

    private MenuItem lastSelectedNavMenuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate references to UI components
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationMenu = navigationView.getMenu();
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        toolbar.setTitle("Dashboard");
        MenuItem dashboardMenuItem = navigationMenu.add("Dashboard");
        dashboardMenuItem.setChecked(true);
        lastSelectedNavMenuItem = dashboardMenuItem;

        // Add items to nav menu here. Remember to actually implement them down below.
        navigationMenu.add("Add donor listing");
        navigationMenu.add("Add charity listing");
        navigationMenu.add("History");
        navigationMenu.add("Login");
        navigationMenu.add("Register");
        navigationMenu.add("Fortnite");

        // Link functions to nav menu items in the switch/case below.
        // Remember that this is case-sensitive.
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        System.out.println("Pressed " + menuItem);

                        // remove highlights from previous item
                        if (lastSelectedNavMenuItem != null)
                            lastSelectedNavMenuItem.setChecked(false);

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        lastSelectedNavMenuItem = menuItem;

                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        String itemName = menuItem.getTitle().toString();
                        switch (itemName) {
                            case "Dashboard":
                                break;
                            case "Login":
                                break;
                            case "Register":
                                break;
                            default:
                                String error = "ERROR: No function implemented for " + itemName;
                                showDialog(error);
                                System.out.println(error);
                        }

                        return true;
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        System.out.println("Pressed " + item);
        // Break is not needed for this switch case because return is used.
        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    // Created to avoid shenanigans with inner classes
    public void showDialog(String str) {
        Utils.showDialog(str, this);
    }

}

//YEET TEST COMMENT - VONG