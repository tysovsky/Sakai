package com.sky.sakai.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.sky.sakai.R;
import com.sky.sakai.models.Site;
import com.sky.sakai.models.User;
import com.sky.sakai.network.NetworkManager;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NetworkManager.OnClassesRequestListener {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        User user = (User)getIntent().getSerializableExtra("user");

        TextView tvFullName = navigationView.getHeaderView(0).findViewById(R.id.nav_full_name);
        TextView tvEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_email);

        tvFullName.setText(user.FullName);
        tvEmail.setText(user.NetId + "@scarletmail.rutgers.edu");

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_announcements, R.id.nav_assignments)
                .setDrawerLayout(drawer)
                .build();

        //mAppBarConfiguration.getDrawerLayout().set
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getTitle().equals(getResources().getString(R.string.menu_assignments))){
                    Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_assignments);
                    return false;
                }

                if (menuItem.getTitle().equals(getResources().getString(R.string.menu_announcements))){
                    Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_announcements);
                    return false;
                }


                Bundle bundle = new Bundle();
                //bundle.putSerializable("announcement", adapter.getItem(position));
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_class);
                //Navigation.findNavController(view).navigate(R.id.nav_announcement_details, bundle);
                return false;
            }
        });


        NetworkManager.getInstance().getSites(this);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onClassesReceived(ArrayList<Site> sites) {

        final HashMap<String, ArrayList<Site>> sitesByTerm = new HashMap<>();

        for (int i = 0; i < sites.size(); i++){

            String term = sites.get(i).Term;

            if(sitesByTerm.containsKey(term)){
                sitesByTerm.get(term).add(sites.get(i));
            }
            else{
                sitesByTerm.put(term, new ArrayList<Site>());
            }

        }

        final ArrayList<String> terms = new ArrayList<>(sitesByTerm.keySet());

        Collections.sort(terms, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                String[] s1 = o1.split(" ");
                String[] s2= o2.split(" ");

                Integer term1 = Integer.parseInt(s1[1]);
                Integer term2 = Integer.parseInt(s2[1]);

                if(term1 == term2){

                    if (s1[0].equals("Spring")){
                        return -1;
                    }
                    else{
                        return 1;
                    }

                }
                else{
                    return term2.compareTo(term1);
                }


            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {



                Menu m = navigationView.getMenu();

                for (String key : terms) {
                    SubMenu termGroup = m.addSubMenu(key);

                    for (int i = 0; i < sitesByTerm.get(key).size(); i++){
                        termGroup.add(sitesByTerm.get(key).get(i).Title);
                    }
                }
            }
        });
    }
}
