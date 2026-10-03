package br.com.robodegaragem.camel_aula1.route.pedido;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PedidoApiRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("platform-http");
        rest().openApi("openapi/pedidos-api.yaml");
    }
}