package model;

import java.util.Date;

public class OrdemReparo {

    private int id;
    private int clienteId;
    private int servicoId;
    private Date dataEntrada;
    private Date dataPrevisaoEntrega;
    private Date dataConclusao;

    private String dispositivoModelo;
    private String problemaRelatado;
    private String status;
    private double valorTotal;
    private String observacoes;

    public OrdemReparo() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getServicoId() { return servicoId; }
    public void setServicoId(int servicoId) { this.servicoId = servicoId; }

    public Date getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(Date dataEntrada) { this.dataEntrada = dataEntrada; }

    public Date getDataPrevisaoEntrega() { return dataPrevisaoEntrega; }
    public void setDataPrevisaoEntrega(Date dataPrevisaoEntrega) { this.dataPrevisaoEntrega = dataPrevisaoEntrega; }

    public Date getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(Date dataConclusao) { this.dataConclusao = dataConclusao; }

    public String getDispositivoModelo() { return dispositivoModelo; }
    public void setDispositivoModelo(String dispositivoModelo) { this.dispositivoModelo = dispositivoModelo; }

    public String getProblemaRelatado() { return problemaRelatado; }
    public void setProblemaRelatado(String problemaRelatado) { this.problemaRelatado = problemaRelatado; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
