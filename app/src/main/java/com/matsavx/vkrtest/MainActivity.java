package com.matsavx.vkrtest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vkrtest.R;
import com.example.vkrtest.databinding.ActivityMainBinding;
//import com.matsavx.vkrtest.configures.ConfProperties;
import com.matsavx.vkrtest.fragments.HomeFragment;
import com.matsavx.vkrtest.fragments.MapFragment;
import com.matsavx.vkrtest.fragments.SensorFragment;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MapKitFactory.setApiKey(ConfProperties.getProperty("yandex_api_key"));
        MapKitFactory.setApiKey("8d56adda-d18d-4724-b294-2b27e9ce5a6f");
        MapKitFactory.initialize(this);
//        DirectionsFactory.initialize(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_page_id:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.map_page_id:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.sensor_page_id:
                    replaceFragment(new SensorFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();

    }
}