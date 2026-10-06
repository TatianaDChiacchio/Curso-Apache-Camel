package br.com.robodegaragem.camel_aula1.route.pedido;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExcluirPedidoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:excluirPedido")
                .routeId("excluir-pedido")
                .log("Excluindo pedido ID: ${header.id}")
                .convertHeaderTo("id", Long.class)
                .to("sql:classpath:sql/excluir-pedido.sql")
                .log("Quantidade de registros excluídos: ${header.CamelSqlUpdateCount}")
                .choice()
                    .when(header("CamelSqlUpdateCount").isEqualTo(0))
                    .log("Pedido não encontrado: ${header.id}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                    .setBody(constant("{\"mensagem\":\"Pedido não encontrado\"}"))
                    .setHeader("Content-Type", constant("application/json"))
                .otherwise()
                    .log("Pedido excluído com sucesso: ${header.id}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                    .setBody(constant(null))
                .end();
    }
}