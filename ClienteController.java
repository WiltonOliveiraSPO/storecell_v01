package controller;

import dao.ClienteDAO;
import model.Cliente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClienteController {

    private final ClienteDAO dao;

    public ClienteController(Connection conn) {
        this.dao = new ClienteDAO(conn);
    }

    // =========================
    // CRUD
    // =========================

    public int salvar(Cliente c) throws SQLException {
        if (c.getClienteId() == 0) {
            return dao.inserir(c);
        } else {
            boolean ok = dao.atualizar(c);
            return ok ? c.getClienteId() : 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        return dao.excluir(id);
    }

    // =========================
    // BUSCAS
    // =========================

    public Cliente buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public List<Cliente> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public List<Cliente> buscarPorNome(String nome) throws SQLException {
        return dao.buscarPorNome(nome);
    }
}
