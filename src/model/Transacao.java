package model;

import java.sql.Timestamp;

public class Transacao {
    private String tipoTransacao;   
    private double valor;          
    private Timestamp dataHora;     

    // Construtor com todos os parâmetros
    public Transacao(String tipoTransacao, double valor, Timestamp dataHora) {
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(String tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "tipoTransacao='" + tipoTransacao + '\'' +
                ", valor=" + valor +
                ", dataHora=" + dataHora +
                '}';
    }
}
