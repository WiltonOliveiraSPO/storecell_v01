package dao;

import model.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    private Connection conn;

    public ServicoDAO(Connection conn) {
        this.conn = conn;
    }

    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servicos (nome, preco) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, s.getNome());
        stmt.setDouble(2, s.getPreco());
        stmt.execute();
        stmt.close();
    }

    public void atualizar(Servico s) throws SQLException {
        String sql = "UPDATE servicos SET nome=?, preco=? WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, s.getNome());
        stmt.setDouble(2, s.getPreco());
        stmt.setInt(3, s.getId());
        stmt.execute();
        stmt.close();
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servicos WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.execute();
        stmt.close();
    }

    public List<Servico> listar() throws SQLException {
        String sql = "SELECT * FROM servicos ORDER BY nome";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        List<Servico> lista = new ArrayList<>();

        while (rs.next()) {
            Servico s = new Servico(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDouble("preco")
            );
            lista.add(s);
        }

        rs.close();
        stmt.close();
        return lista;
    }
}
