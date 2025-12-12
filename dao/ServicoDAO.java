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

    // =====================================================
    // INSERT
    // =====================================================
    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servicos (nome, descricao, valor, tempo_estimado_horas) "
                   + "VALUES (?, ?, ?, ?)";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, s.getNome());
        pst.setString(2, s.getDescricao());
        pst.setDouble(3, s.getValor());
        pst.setDouble(4, s.getTempoEstimadoHoras());

        pst.execute();
        pst.close();
    }

    // =====================================================
    // UPDATE
    // =====================================================
    public void atualizar(Servico s) throws SQLException {
        String sql = "UPDATE servicos SET nome=?, descricao=?, valor=?, tempo_estimado_horas=? "
                   + "WHERE servico_id=?";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, s.getNome());
        pst.setString(2, s.getDescricao());
        pst.setDouble(3, s.getValor());
        pst.setDouble(4, s.getTempoEstimadoHoras());
        pst.setInt(5, s.getId());

        pst.execute();
        pst.close();
    }

    // =====================================================
    // DELETE
    // =====================================================
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servicos WHERE servico_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, id);
        pst.execute();
        pst.close();
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================
    public List<Servico> listar() throws SQLException {
        String sql = "SELECT servico_id, nome, descricao, valor, tempo_estimado_horas "
                   + "FROM servicos ORDER BY nome";

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        List<Servico> lista = new ArrayList<>();

        while (rs.next()) {
            Servico s = new Servico(
                    rs.getInt("servico_id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getDouble("valor"),
                    rs.getDouble("tempo_estimado_horas")
            );
            lista.add(s);
        }

        rs.close();
        pst.close();
        return lista;
    }

    // =====================================================
    // BUSCAR POR NOME (LIKE)
    // =====================================================
    public List<Servico> buscarPorNome(String parteNome) throws SQLException {
        String sql = "SELECT servico_id, nome, descricao, valor, tempo_estimado_horas "
                   + "FROM servicos WHERE nome LIKE ? ORDER BY nome";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + parteNome + "%");
        ResultSet rs = pst.executeQuery();

        List<Servico> lista = new ArrayList<>();

        while (rs.next()) {
            Servico s = new Servico(
                    rs.getInt("servico_id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getDouble("valor"),
                    rs.getDouble("tempo_estimado_horas")
            );
            lista.add(s);
        }

        rs.close();
        pst.close();
        return lista;
    }

    // =====================================================
    // BUSCAR POR ID (usado na navegação e edição)
    // =====================================================
    public Servico buscarPorId(int id) throws SQLException {
        String sql = "SELECT servico_id, nome, descricao, valor, tempo_estimado_horas "
                   + "FROM servicos WHERE servico_id=?";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, id);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Servico s = new Servico(
                    rs.getInt("servico_id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getDouble("valor"),
                    rs.getDouble("tempo_estimado_horas")
            );
            rs.close();
            pst.close();
            return s;
        }

        rs.close();
        pst.close();
        return null;
    }
}
