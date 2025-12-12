package dao;

import model.OrdemReparo;
import model.RelatorioOrdem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class OrdemReparoDAO {

    private Connection conn;

    public OrdemReparoDAO(Connection conn) {
        this.conn = conn;
    }

    public void inserir(OrdemReparo o) throws SQLException {
        String sql = "INSERT INTO ordens_reparo (cliente_id, servico_id, data_entrada, data_previsao_entrega, " +
                "data_conclusao, dispositivo_modelo, problema_relatado, status, valor_total, observacoes) " +
                "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setInt(1, o.getClienteId());
        stmt.setInt(2, o.getServicoId());
        stmt.setDate(3, new java.sql.Date(o.getDataPrevisaoEntrega().getTime()));
        stmt.setDate(4, o.getDataConclusao() == null ? null : new java.sql.Date(o.getDataConclusao().getTime()));
        stmt.setString(5, o.getDispositivoModelo());
        stmt.setString(6, o.getProblemaRelatado());
        stmt.setString(7, o.getStatus());
        stmt.setDouble(8, o.getValorTotal());
        stmt.setString(9, o.getObservacoes());

        stmt.execute();
        stmt.close();
    }

    public void atualizar(OrdemReparo o) throws SQLException {
        String sql = "UPDATE ordens_reparo SET cliente_id=?, servico_id=?, data_previsao_entrega=?, data_conclusao=?, " +
                "dispositivo_modelo=?, problema_relatado=?, status=?, valor_total=?, observacoes=? WHERE ordem_id=?";

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setInt(1, o.getClienteId());
        stmt.setInt(2, o.getServicoId());
        stmt.setDate(3, new java.sql.Date(o.getDataPrevisaoEntrega().getTime()));
        stmt.setDate(4, o.getDataConclusao() == null ? null : new java.sql.Date(o.getDataConclusao().getTime()));
        stmt.setString(5, o.getDispositivoModelo());
        stmt.setString(6, o.getProblemaRelatado());
        stmt.setString(7, o.getStatus());
        stmt.setDouble(8, o.getValorTotal());
        stmt.setString(9, o.getObservacoes());
        stmt.setInt(10, o.getId());

        stmt.execute();
        stmt.close();
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM ordens_reparo WHERE ordem_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.execute();
        stmt.close();
    }

    public List<OrdemReparo> listar() throws SQLException {
        String sql = "SELECT * FROM ordens_reparo ORDER BY ordem_id DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        List<OrdemReparo> lista = new ArrayList<>();

        while (rs.next()) {
            OrdemReparo o = new OrdemReparo();

            o.setId(rs.getInt("ordem_id"));
            o.setClienteId(rs.getInt("cliente_id"));
            o.setServicoId(rs.getInt("servico_id"));
            o.setDataEntrada(rs.getTimestamp("data_entrada"));
            o.setDataPrevisaoEntrega(rs.getDate("data_previsao_entrega"));
            o.setDataConclusao(rs.getTimestamp("data_conclusao"));
            o.setDispositivoModelo(rs.getString("dispositivo_modelo"));
            o.setProblemaRelatado(rs.getString("problema_relatado"));
            o.setStatus(rs.getString("status"));
            o.setValorTotal(rs.getDouble("valor_total"));
            o.setObservacoes(rs.getString("observacoes"));

            lista.add(o);
        }

        rs.close();
        stmt.close();
        return lista;
    }
    
 // importe model.RelatorioOrdem e java.util.*
    public List<RelatorioOrdem> listarPorStatus(String statusFiltro) throws SQLException {
        String sql =
          "SELECT o.ordem_id, c.nome AS cliente_nome, s.nome AS servico_nome, " +
          "o.dispositivo_modelo, o.status, o.valor_total, o.data_entrada " +
          "FROM ordens_reparo o " +
          "LEFT JOIN clientes c ON o.cliente_id = c.cliente_id " +
          "LEFT JOIN servicos s ON o.servico_id = s.servico_id " +
          "WHERE o.status = ? ORDER BY o.data_entrada DESC";

        List<RelatorioOrdem> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, statusFiltro);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    RelatorioOrdem r = new RelatorioOrdem();
                    r.setOrdemId(rs.getInt("ordem_id"));
                    r.setClienteNome(rs.getString("cliente_nome"));
                    r.setServicoNome(rs.getString("servico_nome"));
                    r.setDispositivoModelo(rs.getString("dispositivo_modelo"));
                    r.setStatus(rs.getString("status"));
                    r.setValorTotal(rs.getDouble("valor_total"));
                    r.setDataEntrada(rs.getTimestamp("data_entrada"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }

}
