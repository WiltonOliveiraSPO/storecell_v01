package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Produto;

public class ProdutoDAO {

    private Connection conn;

    public ProdutoDAO(Connection conn) {
        this.conn = conn;
    }

    // ==============================
    // INSERT
    // ==============================
    public boolean inserir(Produto p) {
        String sql = "INSERT INTO produtos (nome, descricao, preco_custo, preco_venda, quantidade_estoque, codigo_barras) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setDouble(3, p.getPrecoCusto());
            stmt.setDouble(4, p.getPrecoVenda());
            stmt.setInt(5, p.getQuantidadeEstoque());
            stmt.setString(6, p.getCodigoBarras());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean atualizar(Produto p) {
        String sql = "UPDATE produtos SET nome=?, descricao=?, preco_custo=?, preco_venda=?, "
                   + "quantidade_estoque=?, codigo_barras=? WHERE produto_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setDouble(3, p.getPrecoCusto());
            stmt.setDouble(4, p.getPrecoVenda());
            stmt.setInt(5, p.getQuantidadeEstoque());
            stmt.setString(6, p.getCodigoBarras());
            stmt.setInt(7, p.getProdutoId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public boolean excluir(int id) {
        String sql = "DELETE FROM produtos WHERE produto_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // LISTAR TODOS
    // ==============================
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setProdutoId(rs.getInt("produto_id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setPrecoCusto(rs.getDouble("preco_custo"));
                p.setPrecoVenda(rs.getDouble("preco_venda"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                p.setCodigoBarras(rs.getString("codigo_barras"));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ==============================
    // BUSCAR POR ID
    // ==============================
    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE produto_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Produto p = new Produto();
                p.setProdutoId(rs.getInt("produto_id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setPrecoCusto(rs.getDouble("preco_custo"));
                p.setPrecoVenda(rs.getDouble("preco_venda"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                p.setCodigoBarras(rs.getString("codigo_barras"));

                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

	public Produto buscarPorNomeOuCodigo(String busca) {
    try {
        String sql;

        // Verifica se é número (busca por ID)
        if (busca.matches("\\d+")) {
            sql = "SELECT * FROM produtos WHERE id = ?";
        } else {
            sql = "SELECT * FROM produtos WHERE nome LIKE ?";
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        if (busca.matches("\\d+")) {
            stmt.setInt(1, Integer.parseInt(busca));
        } else {
            stmt.setString(1, "%" + busca + "%");
        }

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Produto p = new Produto();
            p.setProdutoId(rs.getInt("id"));
            p.setNome(rs.getString("nome"));
            p.setPrecoVenda(rs.getDouble("preco_venda"));
            // adicione outros campos se existirem
            return p;
        }

        return null;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

}
