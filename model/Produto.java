package model;

public class Produto {

    private int produtoId;
    private String nome;
    private String descricao;
    private double precoCusto;
    private double precoVenda;
    private int quantidadeEstoque;
    private String codigoBarras;

    public Produto() {}

    public Produto(int produtoId, String nome, String descricao, double precoCusto,
                   double precoVenda, int quantidadeEstoque, String codigoBarras) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.descricao = descricao;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.codigoBarras = codigoBarras;
    }

    public int getProdutoId() {
        return produtoId;
    }
    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }
    public void setPrecoCusto(double precoCusto) {
        this.precoCusto = precoCusto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }
    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

}
