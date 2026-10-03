package br.com.robodegaragem.camel_aula1.route.pedido;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ListarPedidosRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:listarPedidos")
                .routeId("listar-pedidos")
                .log("Listando pedidos");
    }
}