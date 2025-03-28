package com.example.currencyconverter_np1;

import android.content.Context;

import com.example.currencyconverter_np1.Interfaces.IBCBCallback;
import com.example.currencyconverter_np1.models.CurrencyModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BCBRequest {
    private Context context;
    private List<CurrencyModel> currency;


    public BCBRequest(Context context) {
        this.context = context;
    }

    public void Request(String coin, IBCBCallback callback) {
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        String date = formatter.format(dateNow);

        String url = MessageFormat.format(
                "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)?@moeda=''{0}''&@dataCotacao=''{1}''&$top=100&$format=json&$select=cotacaoCompra,dataHoraCotacao,tipoBoletim",
                coin,
                date
        );

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                callback.onFailure("Erro ao buscar os dados: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resposta = response.body().string();
                    List<CurrencyModel> currencyList = convertJsonToList(resposta);
                    callback.onSuccess(currencyList);
                } else {
                    callback.onFailure("Erro na resposta do servidor.");
                }
            }
        });
    }

    private List<CurrencyModel> convertJsonToList(String json) {
        Gson gson = new Gson();

        Type tipoLista = new TypeToken<List<CurrencyModel>>() {}.getType();

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("value");

        return gson.fromJson(jsonArray, tipoLista);
    }
}
