package dao;

import model.Venda;
import java.sql.*;

public class VendaDAO {

    private Connection conn;

    public VendaDAO(Connection conn) {
        this.conn = conn;
    }

    public int inserir(Venda v) throws SQLException {
        String sql = "INSERT INTO vendas(cliente_id, valor_total, desconto, metodo_pagamento) VALUES (?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, v.getClienteId());
        stmt.setDouble(2, v.getValorTotal());
        stmt.setDouble(3, v.getDesconto());
        stmt.setString(4, v.getMetodoPagamento());
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next())
            return rs.getInt(1);

        return -1;
    }
}
