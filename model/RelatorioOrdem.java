package model;

import java.util.Date;

public class RelatorioOrdem {
    private int ordemId;
    private String clienteNome;
    private String servicoNome;
    private String dispositivoModelo;
    private String status;
    private double valorTotal;
    private Date dataEntrada;

    public int getOrdemId() { return ordemId; }
    public void setOrdemId(int ordemId) { this.ordemId = ordemId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getServicoNome() { return servicoNome; }
    public void setServicoNome(String servicoNome) { this.servicoNome = servicoNome; }

    public String getDispositivoModelo() { return dispositivoModelo; }
    public void setDispositivoModelo(String dispositivoModelo) { this.dispositivoModelo = dispositivoModelo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public Date getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(Date dataEntrada) { this.dataEntrada = dataEntrada; }
}
