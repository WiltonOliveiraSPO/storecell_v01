package model;

import java.util.Date;

public class OrdemReparoView {

    private int id;
    private String cliente;
    private String servico;
    private String modelo;
    private String status;
    private double valor;
    private Date dataEntrada;

    // getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public Date getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(Date dataEntrada) { this.dataEntrada = dataEntrada; }
}
