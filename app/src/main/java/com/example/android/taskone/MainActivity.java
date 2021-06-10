package com.example.android.taskone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchFragment searchFragment = new SearchFragment();
        loadFragment(searchFragment,false);

    }

    public void loadFragment(Fragment fragment, Boolean shouldAddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if(shouldAddToBackStack) {
            transaction.addToBackStack("");
        }
        transaction.commit();
    }

    public void dropFragment(Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.remove(fragment);
        trans.commit();
        getSupportFragmentManager().popBackStack();
    }

}