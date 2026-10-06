package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.generated.model.ErroPedido;
import br.com.robodegaragem.camel_aula1.generated.model.NovoPedido;
import br.com.robodegaragem.camel_aula1.mapper.PedidoMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CriarPedidoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:criarPedido")
                .routeId("criar-pedido")
                .log("JSON recebido: ${body}")
                .unmarshal()
                    .json(NovoPedido.class)
                .choice()
                .when(simple("${body.getValor()} >= 5000"))
                        .log("Pedido de ALTO VALOR identificado: ${body}")
                        .to("direct:pedidoAltoValor")
                    .otherwise()
                        .log("Pedido NORMAL identificado: ${body}")
                        .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true))
                        .to("sql:classpath:sql/inserir-pedido.sql")
                        .bean(PedidoMapper.class,"fromGeneratedKeys(${header.CamelSqlGeneratedKeyRows})")
                        .marshal()
                            .json()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                        .setHeader("Content-Type", constant("application/json"))
                .end();

        from("direct:pedidoAltoValor")
                .routeId("pedido-alto-valor")
                .log("Pedido rejeitado por possuir valor acima do permitido: ${body}")

                .process(exchange -> {
                    ErroPedido erro = new ErroPedido();
                    erro.setErro("PEDIDO_ALTO_VALOR");
                    erro.setMensagem(
                            "Pedido não criado. O valor informado excede o limite permitido de R$ 5.000,00."
                    );

                    exchange.getMessage().setBody(erro);
                })

                .marshal()
                    .json()

                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));
    }
}