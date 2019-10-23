package com.example.trackingapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button buttonmap;
    TextView locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationEditText = findViewById(R.id.locationTextView);
        buttonmap=findViewById(R.id.buttonmap);
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }
}

