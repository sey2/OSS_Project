package org.techtown.osshango;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.osshango.Data.UserInfoData;
import org.techtown.osshango.ViewModel.TravelViewModel;

public class MainActivity extends AppCompatActivity {

    private NavController nav_host;
    private BottomNavigationView nav_bar;
    private TravelViewModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Activity2", "MainActivity");

        nav_host = Navigation.findNavController(this, R.id.nav_host);
        nav_bar = findViewById(R.id.nav_bar);
        NavigationUI.setupWithNavController(nav_bar, nav_host);

    }


}