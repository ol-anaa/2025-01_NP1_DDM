package com.example.currencyconverter_np1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private Button BtnSalary;
    private Button BtnConverter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        getWindow().setStatusBarColor(Color.parseColor("#B18F45"));

        BtnSalary = findViewById(R.id.salary);
        BtnConverter = findViewById(R.id.converter);

        BtnConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CurrencyConverter.class);
                startActivity(intent);
            }
        });

        BtnSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Salary.class);
                startActivity(intent);
            }
        });
    }
}