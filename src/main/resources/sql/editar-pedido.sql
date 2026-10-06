UPDATE pedido
SET
    produto = :#${body.getProduto()},
    quantidade = :#${body.getQuantidade()},
    valor = :#${body.getValor()}
WHERE id = :#id