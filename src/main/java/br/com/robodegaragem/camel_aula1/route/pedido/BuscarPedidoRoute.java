package br.com.robodegaragem.camel_aula1.route.pedido;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BuscarPedidoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:buscarPedido")
                .routeId("buscar-pedido")
                .log("Buscando pedido ID: ${header.id}")
                .convertHeaderTo("id", Long.class)
                .to("sql:classpath:sql/buscar-pedido.sql?outputType=SelectOne")
                .choice()
                    .when(body().isNull())
                        .log("Pedido não encontrado: ${header.id}")
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                        .setBody(constant("{\"mensagem\":\"Pedido não encontrado\"}"))
                    .otherwise()
                        .log("Pedido encontrado: ${body}")
                        .marshal()
                            .json()
                .end()
                .setHeader("Content-Type", constant("application/json"));
    }
}