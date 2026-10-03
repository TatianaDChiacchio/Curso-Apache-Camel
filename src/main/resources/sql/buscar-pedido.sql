SELECT
    id,
    produto,
    quantidade,
    valor
FROM pedido
WHERE id = :#id