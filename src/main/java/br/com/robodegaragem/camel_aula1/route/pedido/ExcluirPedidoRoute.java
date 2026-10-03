package br.com.robodegaragem.camel_aula1.route.pedido;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExcluirPedidoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:excluirPedido")
                .routeId("excluir-pedido")
                .log("Excluindo pedido ID: ${header.id}");
    }
}