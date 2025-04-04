package com.example.currencyconverter_np1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class Salary extends AppCompatActivity {
    private Button Btn;
    private EditText Salary;
    private EditText Percentage;
    private TextView LabelResult;
    private EditText Result;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        getWindow().setStatusBarColor(Color.parseColor("#B18F45"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Salary.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        Btn = findViewById(R.id.btn);
        Salary = findViewById(R.id.Salary);
        Percentage = findViewById(R.id.percentage);
        LabelResult = findViewById(R.id.labelResultSalary);
        Result = findViewById(R.id.result);

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValid = validarForm();

                if(!isValid)
                    return;

                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(im != null){
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                String valueSalary = Salary.getText().toString();
                double salary = Double.parseDouble(valueSalary);

                String valuePercentage = Percentage.getText().toString();
                double percentage = Integer.parseInt(valuePercentage);

                double result = salary * (percentage / 100);

                double newSalary = salary + result;

                LabelResult.setVisibility(View.VISIBLE);
                Result.setVisibility(View.VISIBLE);

                String value = String.format("%.2f", newSalary);
                String r = "R$ " + value;
                Result.setText(r);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private boolean validarForm() {
        String salary = Salary.getText().toString().trim();
        String percentage = Percentage.getText().toString().trim();

        boolean error = false;

        if(salary.isBlank()){
            Salary.setError("Você precisa digitar um valor válido para o salário");
            error = true;
        }
        else{
            double doubleValue = Double.parseDouble(salary);

            if (doubleValue <= 0) {
                Salary.setError("Você precisa digitar um valor válido para o salário");
                error = true;
            }
        }

        if(percentage.isBlank()){
            Percentage.setError("Você precisa digitar um percentual válido para aumento salárial");
            error = true;
        }
        else{
            int percentageValue = Integer.parseInt(percentage);

            if (percentageValue <= 0) {
                Percentage.setError("Você precisa digitar um percentual válido para aumento salárial");
                error = true;
            }
        }

        if(!error){
            Toast.makeText(this, "Formulário enviado com sucesso!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return false;
    }
}
