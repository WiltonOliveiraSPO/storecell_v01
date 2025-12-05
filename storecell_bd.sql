use storecell;

CREATE TABLE clientes (
    cliente_id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(15),
    email VARCHAR(100),
    cpf VARCHAR(14) UNIQUE,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE produtos (
    produto_id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco_custo DECIMAL(10, 2) NOT NULL,
    preco_venda DECIMAL(10, 2) NOT NULL,
    quantidade_estoque INT NOT NULL DEFAULT 0,
    codigo_barras VARCHAR(50) UNIQUE,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE servicos (
    servico_id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    valor DECIMAL(10, 2) NOT NULL,
    tempo_estimado_horas DECIMAL(5, 2) DEFAULT 1.00
);

CREATE TABLE vendas (
    venda_id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id INT,
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL,
    desconto DECIMAL(10, 2) DEFAULT 0.00,
    metodo_pagamento VARCHAR(50) NOT NULL, -- Ex: Cartão, Dinheiro, PIX
    FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id)
);

CREATE TABLE itens_venda (
    item_venda_id INT PRIMARY KEY AUTO_INCREMENT,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (venda_id) REFERENCES vendas(venda_id),
    FOREIGN KEY (produto_id) REFERENCES produtos(produto_id)
);

CREATE TABLE ordens_reparo (
    ordem_id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id INT NOT NULL,
    servico_id INT, -- Referencia o tipo de serviço geral
    data_entrada DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_previsao_entrega DATE,
    data_conclusao DATETIME,
    dispositivo_modelo VARCHAR(100) NOT NULL, -- Ex: Samsung A50
    problema_relatado TEXT NOT NULL,
    status ENUM('Aberto', 'Em Reparo', 'Aguardando Peça', 'Pronto', 'Entregue') NOT NULL DEFAULT 'Aberto',
    valor_total DECIMAL(10, 2) NOT NULL,
    observacoes TEXT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id),
    FOREIGN KEY (servico_id) REFERENCES servicos(servico_id)
);

