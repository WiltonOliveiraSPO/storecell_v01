package model;

import java.time.LocalDateTime;

public class Cliente {
    private int clienteId;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    private LocalDateTime dataCadastro;

    public Cliente() {}

    public Cliente(int clienteId, String nome, String telefone, String email, String cpf, LocalDateTime dataCadastro) {
        this.clienteId = clienteId;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cpf = cpf;
        this.dataCadastro = dataCadastro;
    }

    // getters e setters
    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return clienteId + " - " + nome;
    }
}
