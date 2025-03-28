package com.example.currencyconverter_np1.Interfaces;

import com.example.currencyconverter_np1.models.CurrencyModel;

import java.util.List;

public interface IBCBCallback {
    void onSuccess(List<CurrencyModel> currencyList);
    void onFailure(String errorMessage);
}
