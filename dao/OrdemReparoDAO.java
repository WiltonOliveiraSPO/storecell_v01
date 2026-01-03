package dao;

import model.OrdemReparo;
import model.OrdemReparoView;
import model.RelatorioOrdem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemReparoDAO {

    private Connection conn;

    public OrdemReparoDAO(Connection conn) {
        this.conn = conn;
    }

    /* =========================
       INSERIR
       ========================= */
    public void inserir(OrdemReparo o) throws SQLException {

    	String sql = """
    		    INSERT INTO ordens_reparo (
    		        cliente_id,
    		        servico_id,
    		        dispositivo_modelo,
    		        status,
    		        valor_total,
    		        problema_relatado,
    		        observacoes,
    		        data_previsao_entrega,
    		        data_conclusao
    		    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    		""";


    	PreparedStatement ps = conn.prepareStatement(sql);

    	ps.setInt(1, o.getClienteId());
    	ps.setInt(2, o.getServicoId());
    	ps.setString(3, o.getDispositivoModelo());
    	ps.setString(4, o.getStatus());
    	ps.setDouble(5, o.getValorTotal());
    	ps.setString(6, o.getProblemaRelatado());
    	ps.setString(7, o.getObservacoes());

    	// 👉 AQUI ENTRA O BLOCO 👇
    	if (o.getDataPrevisaoEntrega() != null) {
    	    ps.setDate(8, (Date) o.getDataPrevisaoEntrega());
    	} else {
    	    ps.setNull(8, java.sql.Types.DATE);
    	}

    	if (o.getDataConclusao() != null) {
    	    ps.setDate(9, (Date) o.getDataConclusao());
    	} else {
    	    ps.setNull(9, java.sql.Types.DATE);
    	}

    	ps.executeUpdate();
        
    }

    /* =========================
       EXCLUIR
       ========================= */
    public void excluir(int id) throws SQLException {

        String sql = "DELETE FROM ordens_reparo WHERE ordem_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }
    
    public void atualizar(OrdemReparo o) throws SQLException {

    	String sql = """
    		    UPDATE ordens_reparo SET
    		        cliente_id = ?,
    		        servico_id = ?,
    		        dispositivo_modelo = ?,
    		        status = ?,
    		        valor_total = ?,
    		        problema_relatado = ?,
    		        observacoes = ?,
    		        data_previsao_entrega = ?,
    		        data_conclusao = ?
    		    WHERE servico_id = ?
    		""";

    	PreparedStatement ps = conn.prepareStatement(sql);

    	ps.setInt(1, o.getClienteId());
    	ps.setInt(2, o.getServicoId());
    	ps.setString(3, o.getDispositivoModelo());
    	ps.setString(4, o.getStatus());
    	ps.setDouble(5, o.getValorTotal());
    	ps.setString(6, o.getProblemaRelatado());
    	ps.setString(7, o.getObservacoes());

    	// 👉 MESMO BLOCO AQUI 👇
    	if (o.getDataPrevisaoEntrega() != null) {
    	    ps.setDate(8, (Date) o.getDataPrevisaoEntrega());
    	} else {
    	    ps.setNull(8, java.sql.Types.DATE);
    	}

    	if (o.getDataConclusao() != null) {
    	    ps.setDate(9, (Date) o.getDataConclusao());
    	} else {
    	    ps.setNull(9, java.sql.Types.DATE);
    	}

    	// ID DA OS
    	ps.setInt(10, o.getId());

    	ps.executeUpdate();
    }


    /* =========================
       BUSCAR POR ID
       ========================= */
    public OrdemReparo buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT 
                o.id,
                o.cliente_id,
                o.servico_id,
                o.dispositivo_modelo,
                o.problema_relatado,
                o.status,
                o.valor_total,
                o.observacoes,
                o.data_previsao_entrega,
                o.data_conclusao,
                o.data_entrada
            FROM ordens_reparo o
            WHERE o.id = ?
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            OrdemReparo o = new OrdemReparo();
            o.setId(rs.getInt("id"));
            o.setClienteId(rs.getInt("cliente_id"));
            o.setServicoId(rs.getInt("servico_id"));
            o.setDispositivoModelo(rs.getString("dispositivo_modelo"));
            o.setProblemaRelatado(rs.getString("problema_relatado"));
            o.setStatus(rs.getString("status"));
            o.setValorTotal(rs.getDouble("valor_total"));
            o.setObservacoes(rs.getString("observacoes"));
            o.setDataPrevisaoEntrega(rs.getDate("data_previsao_entrega"));
            o.setDataConclusao(rs.getDate("data_conclusao"));
            return o;
        }
        return null;
    }

    /* =========================
       LISTAR (COM JOIN)
       ========================= */
    public List<OrdemReparo> listar() throws SQLException {
        List<OrdemReparo> lista = new ArrayList<>();

        String sql = """
            SELECT 
                ordem_id,
                cliente_id,
                servico_id,
                dispositivo_modelo,
                status,
                valor_total,
                data_entrada
            FROM ordens_reparo
            ORDER BY data_entrada DESC
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            OrdemReparo o = new OrdemReparo();
            o.setId(rs.getInt("ordem_id"));
            o.setClienteId(rs.getInt("cliente_id"));
            o.setServicoId(rs.getInt("servico_id"));
            o.setDispositivoModelo(rs.getString("dispositivo_modelo"));
            o.setStatus(rs.getString("status"));
            o.setValorTotal(rs.getDouble("valor_total"));
            o.setDataEntrada(rs.getTimestamp("data_entrada"));
            lista.add(o);
        }

        return lista;
    }


    /* =========================
       MÉTODO AUXILIAR
       ========================= */
    private OrdemReparo montarOrdem(ResultSet rs) throws SQLException {

        OrdemReparo o = new OrdemReparo();

        o.setId(rs.getInt("ordem_id"));
        o.setClienteId(rs.getInt("cliente_id"));
        o.setServicoId(rs.getInt("servico_id"));
        o.setDispositivoModelo(rs.getString("dispositivo_modelo"));
        o.setProblemaRelatado(rs.getString("problema_relatado"));
        o.setStatus(rs.getString("status"));
        o.setValorTotal(rs.getDouble("valor_total"));
        o.setObservacoes(rs.getString("observacoes"));
        o.setDataEntrada(rs.getDate("data_entrada"));
        o.setDataPrevisaoEntrega(rs.getDate("data_previsao_entrega"));
        o.setDataConclusao(rs.getDate("data_conclusao"));

        // Campos auxiliares para a tela
        o.setNomeCliente(rs.getString("cliente"));
        o.setNomeServico(rs.getString("servico"));

        return o;
    }

    public List<RelatorioOrdem> listarPorStatus(String status) throws SQLException {
        List<RelatorioOrdem> lista = new ArrayList<>();

        String sql = """
            SELECT o.ordem_id,
                   c.nome AS cliente,
                   s.nome AS servico,
                   o.status,
                   o.valor_total,
                   o.data_entrada
            FROM ordens_reparo o
            JOIN clientes c ON c.cliente_id = o.cliente_id
            JOIN servicos s ON s.servico_id = o.servico_id
            WHERE o.status = ?
            ORDER BY o.data_entrada DESC
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, status);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            RelatorioOrdem r = new RelatorioOrdem();
            r.setOrdemId(rs.getInt("id"));
            r.setClienteNome(rs.getString("cliente"));
            r.setServicoNome(rs.getString("servico"));
            r.setStatus(rs.getString("status"));
            r.setValorTotal(rs.getDouble("valor_total"));
            r.setDataEntrada(rs.getTimestamp("data_entrada"));
            lista.add(r);
        }

        return lista;
    }
    
    public List<OrdemReparoView> listarComJoin() throws SQLException {

        List<OrdemReparoView> lista = new ArrayList<>();

        String sql = """
            SELECT
                o.ordem_id,
                c.nome AS cliente_nome,
                s.nome AS servico_nome,
                o.dispositivo_modelo,
                o.status,
                o.valor_total,
                o.data_entrada
            FROM ordens_reparo o
            JOIN clientes c ON c.cliente_id = o.cliente_id
            JOIN servicos s ON s.servico_id = o.servico_id
            ORDER BY o.data_entrada DESC
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            OrdemReparoView v = new OrdemReparoView();
            v.setId(rs.getInt("ordem_id"));
            v.setCliente(rs.getString("cliente_nome"));
            v.setServico(rs.getString("servico_nome"));
            v.setModelo(rs.getString("dispositivo_modelo"));
            v.setStatus(rs.getString("status"));
            v.setValor(rs.getDouble("valor_total"));
            v.setDataEntrada(rs.getTimestamp("data_entrada"));

            lista.add(v);
        }

        return lista;
    }


}
