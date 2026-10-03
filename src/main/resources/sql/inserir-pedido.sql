INSERT INTO pedido (
    produto,
    quantidade,
    valor
)
VALUES (
           :#${body.getProduto()},
           :#${body.getQuantidade()},
           :#${body.getValor()}
       )