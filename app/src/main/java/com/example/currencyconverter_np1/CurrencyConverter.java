package com.example.currencyconverter_np1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.currencyconverter_np1.Interfaces.IBCBCallback;
import com.example.currencyconverter_np1.models.CurrencyModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class CurrencyConverter extends AppCompatActivity {
    private AutoCompleteTextView CurrencyEntry;
    private AutoCompleteTextView CurrencyExit;
    private EditText ValueInitial;
    private EditText ValueResult;
    private TextView Subtitle;
    private TextView RealQuote;
    private TextView LabelResult;
    private Button Btn;
    private List<String> AllCoins;
    private List<String> EntryCoins;
    private List<String> ExitCoins;
    private String CountryEntry;
    private String CountryExit;
    private Double Price;
    private Double SecondPrice;
    private TextInputLayout FirstCurrencyInputLayout;
    private TextInputLayout SecondCurrencyInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencyconverter);

        configNav();

        //Config dropdown
        CurrencyEntry = findViewById(R.id.currencyEntry);
        CurrencyExit = findViewById(R.id.currencyExit);
        ValueResult = findViewById(R.id.valueEnd);
        ValueInitial = findViewById(R.id.valueInitial);
        LabelResult = findViewById(R.id.labelResult);
        Btn = findViewById(R.id.btnConvert);
        Subtitle = findViewById(R.id.subtitle);
        RealQuote = findViewById(R.id.realQuote);
        FirstCurrencyInputLayout = findViewById(R.id.firstDropDown);
        SecondCurrencyInputLayout = findViewById(R.id.secondDropDown);

        AllCoins = Arrays.asList(getResources().getStringArray(R.array.coins));
        EntryCoins = new ArrayList<>(AllCoins);
        ExitCoins = new ArrayList<>(AllCoins);

        setupAutoCompleteTextView(CurrencyEntry, EntryCoins);
        setupAutoCompleteTextView(CurrencyExit, ExitCoins);

        CurrencyEntry.setOnItemClickListener((parent, view, position, id) -> {
            FirstCurrencyInputLayout.setError(null);

            String selectedItem = EntryCoins.get(position);
            String otherItem = CurrencyExit.getText().toString();

            CountryEntry = selectedItem.substring(0, 3);

            updateLists(selectedItem, otherItem, EntryCoins, ExitCoins);
        });

        CurrencyExit.setOnItemClickListener((parent, view, position, id) -> {
            SecondCurrencyInputLayout.setError(null);

            String selectedItem = ExitCoins.get(position);
            String otherItem = CurrencyEntry.getText().toString();

            CountryExit = selectedItem.substring(0, 3);

            updateLists(selectedItem, otherItem, EntryCoins, ExitCoins);
        });

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

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> showRatingDialog());
                    }
                }, 1000);

                // Case 1: Converter de BRL para outra moeda
                if (CountryEntry.equals("BRL")) {
                    makeBCBRequest(CountryExit, price -> {
                        convertBrazilianCurrencyToForeign(price);
                    }, errorMessage -> {
                        Toast.makeText(CurrencyConverter.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }

                // Case 2: Converter de outra moeda para BRL
                else if (CountryExit.equals("BRL")) {
                    makeBCBRequest(CountryEntry, price -> {
                        convertForeignCurrencyToBrazilian(price);
                    }, errorMessage -> {
                        Toast.makeText(CurrencyConverter.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }

                // Caso 3: Converter entre duas moedas estrangeiras
                else {
                    makeBCBRequest(CountryExit, price -> {
                        Price = price;

                        makeBCBRequest(CountryEntry, secondPrice -> {
                            SecondPrice = secondPrice;
                            converterBetweenForeignCurrencies(Price, SecondPrice);

                        }, errorMessage -> {
                            Toast.makeText(CurrencyConverter.this, errorMessage, Toast.LENGTH_SHORT).show();
                        });

                    }, errorMessage -> {
                        Toast.makeText(CurrencyConverter.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void configNav()
    {
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
                Intent intent = new Intent(CurrencyConverter.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView element, List<String> coins)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, coins);

        element.setAdapter(adapter);
    }

    private void updateLists(String selectedItem, String otherItem, List<String> entryCoins, List<String> exitCoins) {

        entryCoins.remove(selectedItem);
        exitCoins.remove(selectedItem);

        for (String item : AllCoins) {
            if(!entryCoins.contains(item) && !item.equals(selectedItem) && !item.equals(otherItem))
                entryCoins.add(item);
        }

        for (String item : AllCoins) {
            if(!exitCoins.contains(item) && !item.equals(otherItem) && !item.equals(selectedItem))
                exitCoins.add(item);
        }

        ArrayAdapter<String> adapterExit = (ArrayAdapter<String>) CurrencyExit.getAdapter();
        ArrayAdapter<String> adapterEntry = (ArrayAdapter<String>) CurrencyEntry.getAdapter();

        adapterExit.notifyDataSetChanged();
        adapterEntry.notifyDataSetChanged();
    }

    private void convertBrazilianCurrencyToForeign(Double price) {

        String valueInitial = ValueInitial.getText().toString();
        double doubleValue = Double.parseDouble(valueInitial);

        Double result =  doubleValue / price;

        LabelResult.setVisibility(View.VISIBLE);
        ValueResult.setVisibility(View.VISIBLE);

        Subtitle.setVisibility(View.VISIBLE);
        RealQuote.setVisibility(View.VISIBLE);

        String value = String.format("%.2f", result);
        String r = getCurrencySymbol(CountryExit) + " " + value;
        ValueResult.setText(r);

        String priceFormat = String.format("%.2f", price);
        String realQuote = getCurrencySymbol(CountryExit) + priceFormat + " = " + getCurrencySymbol(CountryEntry) + "1.00";
        RealQuote.setText(realQuote);
    }

    private void convertForeignCurrencyToBrazilian(Double price) {

        String valueInitial = ValueInitial.getText().toString();
        double doubleValue = Double.parseDouble(valueInitial);

        Double result = doubleValue * price;

        LabelResult.setVisibility(View.VISIBLE);
        ValueResult.setVisibility(View.VISIBLE);

        Subtitle.setVisibility(View.VISIBLE);
        RealQuote.setVisibility(View.VISIBLE);

        String value = String.format("%.2f", result);
        String r = getCurrencySymbol(CountryExit) + " " + value;
        ValueResult.setText(r);

        String priceFormat = String.format("%.2f", price);

        String realQuote = getCurrencySymbol(CountryExit) + priceFormat  + " = " + getCurrencySymbol(CountryEntry) + "1.00";
        RealQuote.setText(realQuote);
    }

    private void converterBetweenForeignCurrencies(Double price, Double secondPrice) {

        String valueInitial = ValueInitial.getText().toString();
        double doubleValue = Double.parseDouble(valueInitial);

        double crossConversion = secondPrice / price;
        Double result = doubleValue * crossConversion;

        LabelResult.setVisibility(View.VISIBLE);
        ValueResult.setVisibility(View.VISIBLE);

        Subtitle.setVisibility(View.VISIBLE);
        RealQuote.setVisibility(View.VISIBLE);

        String value = String.format("%.2f", result);
        String r = getCurrencySymbol(CountryExit) + " " + value;
        ValueResult.setText(r);

        String priceFormat = String.format("%.2f", crossConversion);

        String realQuote = getCurrencySymbol(CountryExit) + priceFormat + " = " + getCurrencySymbol(CountryEntry) + "1.00";
        RealQuote.setText(realQuote);
    }

    private boolean validarForm() {
        String currencyEntry = CurrencyEntry.getText().toString().trim();
        String currencyExit = CurrencyExit.getText().toString().trim();
        String valueInitial = ValueInitial.getText().toString().trim();

        boolean error = false;

        if (currencyEntry.isBlank() || currencyEntry.equals("Moeda inicial")) {
            CurrencyEntry.dismissDropDown();
            FirstCurrencyInputLayout.setError("Selecione uma moeda para fazer a conversão");
            FirstCurrencyInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
            FirstCurrencyInputLayout.setBoxStrokeErrorColor(ColorStateList.valueOf(Color.RED));

            error = true;
        }

        if (currencyExit.isBlank() || currencyExit.equals("Moeda inicial")) {
            CurrencyExit.dismissDropDown();
            SecondCurrencyInputLayout.setError("Selecione uma moeda para fazer a conversão");
            SecondCurrencyInputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED));
            SecondCurrencyInputLayout.setBoxStrokeErrorColor(ColorStateList.valueOf(Color.RED));

            error = true;
        }

        if(valueInitial.isBlank()){
            ValueInitial.setError("Você precisa digitar um valor válido para conversão");
            error = true;
        }
        else{
            double doubleValue = Double.parseDouble(valueInitial);

            if (valueInitial.isBlank() || doubleValue <= 0) {
                ValueInitial.setError("Você precisa digitar um valor válido para conversão");
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

    private void makeBCBRequest(String countryCode, Consumer<Double> onSuccess, Consumer<String> onFailure) {
        BCBRequest bcbRequest = new BCBRequest(CurrencyConverter.this);
        bcbRequest.Request(countryCode, new IBCBCallback() {
            @Override
            public void onSuccess(List<CurrencyModel> currencyList) {
                runOnUiThread(() -> {
                    Double price = null;
                    for (CurrencyModel currency : currencyList) {
                        if (currency.getTipoBoletim().equals("Fechamento PTAX")) {
                            price = currency.getCotacaoVenda();
                            break;
                        }
                    }

                    if (price == null && !currencyList.isEmpty()) {
                        price = currencyList.get(0).getCotacaoVenda();
                    }

                    onSuccess.accept(price);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> onFailure.accept(errorMessage));
            }
        });
    }

    public String getCurrencySymbol(String currency) {
        switch (currency) {
            case "BRL":
                return "R$";
            case "USD":
                return "$";
            case "AUD":
                return "A$";
            case "EUR":
                return "€";
            case "GBP":
                return "£";
            case "JPY":
                return "¥";
            default:
                return "";
        }
    }

    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_rating, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            Toast.makeText(this, "Você avaliou com " + rating + " estrelas", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}