package com.example.currencyconverter_np1.models;

public class CurrencyModel {
    private double cotacaoCompra;
    private double cotacaoVenda;
    private String dataHoraCotacao;
    private String tipoBoletim;

    public double getCotacaoCompra() {
        return cotacaoCompra;
    }

    public void setCotacaoCompra(double cotacaoCompra) {
        this.cotacaoCompra = cotacaoCompra;
    }

    public double getCotacaoVenda() {
        return cotacaoCompra;
    }

    public void setCotacaoVenda(double cotacaoVenda) {
        this.cotacaoVenda = cotacaoVenda;
    }

    public String getDataHoraCotacao() {
        return dataHoraCotacao;
    }

    public void setDataHoraCotacao(String dataHoraCotacao) {
        this.dataHoraCotacao = dataHoraCotacao;
    }

    public String getTipoBoletim() {
        return tipoBoletim;
    }

    public void setTipoBoletim(String tipoBoletim) {
        this.tipoBoletim = tipoBoletim;
    }
}
