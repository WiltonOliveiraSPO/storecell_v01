package dao;

import model.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // CREATE
    public int inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes (nome, telefone, email, cpf) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, c.getNome());
            pst.setString(2, c.getTelefone());
            pst.setString(3, c.getEmail());
            pst.setString(4, c.getCpf());

            int affected = pst.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setClienteId(id);
                    return id;
                }
            }
            return -1;
        }
    }

    // READ by id
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT cliente_id, nome, telefone, email, cpf, data_cadastro FROM clientes WHERE cliente_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapRowToCliente(rs);
                    return c;
                }
            }
        }
        return null;
    }

    // UPDATE
    public boolean atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE clientes SET nome = ?, telefone = ?, email = ?, cpf = ? WHERE cliente_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, c.getNome());
            pst.setString(2, c.getTelefone());
            pst.setString(3, c.getEmail());
            pst.setString(4, c.getCpf());
            pst.setInt(5, c.getClienteId());

            int affected = pst.executeUpdate();
            return affected > 0;
        }
    }

    // DELETE
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE cliente_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            int affected = pst.executeUpdate();
            return affected > 0;
        }
    }

    // LIST ALL
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT cliente_id, nome, telefone, email, cpf, data_cadastro FROM clientes ORDER BY nome";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToCliente(rs));
            }
        }
        return lista;
    }

    // FIND BY NAME (contains)
    public List<Cliente> buscarPorNome(String parteNome) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT cliente_id, nome, telefone, email, cpf, data_cadastro FROM clientes WHERE nome LIKE ? ORDER BY nome";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + parteNome + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToCliente(rs));
                }
            }
        }
        return lista;
    }

    // mapping helper
    private Cliente mapRowToCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setClienteId(rs.getInt("cliente_id"));
        c.setNome(rs.getString("nome"));
        c.setTelefone(rs.getString("telefone"));
        c.setEmail(rs.getString("email"));
        c.setCpf(rs.getString("cpf"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            c.setDataCadastro(ts.toLocalDateTime());
        } else {
            c.setDataCadastro(LocalDateTime.now());
        }
        return c;
    }
}
