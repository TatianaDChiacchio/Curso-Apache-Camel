package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.processor.CorrelationLogProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ListarPedidosRoute extends RouteBuilder {

    private final CorrelationLogProcessor correlationLogProcessor;

    public ListarPedidosRoute( CorrelationLogProcessor correlationLogProcessor) {

        this.correlationLogProcessor = correlationLogProcessor;

    }

    @Override
    public void configure() throws Exception {

        from("direct:listarPedidos")
                .routeId("listar-pedidos")
                .process(correlationLogProcessor)
                .log("Listando pedidos")
               // .delay(10000)
                .to("sql:classpath:sql/listar-pedidos.sql")
                .marshal()
                    .json()
                .setHeader("Content-Type", constant("application/json"));
    }
}