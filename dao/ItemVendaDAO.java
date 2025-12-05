package dao;

import model.ItemVenda;
import java.sql.*;

public class ItemVendaDAO {

    private Connection conn;

    public ItemVendaDAO(Connection conn) {
        this.conn = conn;
    }

    public void inserir(ItemVenda item) throws SQLException {
        String sql = "INSERT INTO itens_venda(venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, item.getVendaId());
        stmt.setInt(2, item.getProdutoId());
        stmt.setInt(3, item.getQuantidade());
        stmt.setDouble(4, item.getPrecoUnitario());

        stmt.executeUpdate();
    }
}
