package model;

import java.math.BigDecimal;

public class Servico {

    private int id;                     // servico_id
    private String nome;                // nome
    private String descricao;           // descricao
    private double valor;               // valor (decimal 10,2)
    private double tempoEstimadoHoras;  // tempo_estimado_horas (decimal 5,2)

    public Servico() {
    }

    public Servico(int id, String nome, String descricao, double valor, double tempoEstimadoHoras) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.tempoEstimadoHoras = tempoEstimadoHoras;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getTempoEstimadoHoras() {
        return tempoEstimadoHoras;
    }

    public void setTempoEstimadoHoras(double tempoEstimadoHoras) {
        this.tempoEstimadoHoras = tempoEstimadoHoras;
    }
    
    @Override
    public String toString() {
        return nome; // ou: nome + " - R$ " + valor
    }

}
