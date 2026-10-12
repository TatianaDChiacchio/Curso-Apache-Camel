package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.model.ErrorResponseProcessor;
import br.com.robodegaragem.camel_aula1.processor.CorrelationLogProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BuscarPedidoRoute extends RouteBuilder {

    private final ErrorResponseProcessor errorResponseProcessor;
    private final CorrelationLogProcessor correlationLogProcessor;

    public BuscarPedidoRoute(ErrorResponseProcessor errorResponseProcessor,
                             CorrelationLogProcessor correlationLogProcessor) {
        this.errorResponseProcessor = errorResponseProcessor;
        this.correlationLogProcessor = correlationLogProcessor;
    }

    @Override
    public void configure() throws Exception {

        from("direct:buscarPedido")
                .routeId("buscar-pedido")
                .process(correlationLogProcessor)
                .log("Buscando pedido ID: ${header.id}")
                .convertHeaderTo("id", Long.class)
                .to("sql:classpath:sql/buscar-pedido.sql?outputType=SelectOne")
                .choice()
                    .when(body().isNull())
                        .log("Pedido não encontrado: ${header.id}")
                        .process(exchange -> errorResponseProcessor.setError(
                            exchange, 404, "Pedido Not Found", "Pedido " + exchange.getMessage().getHeader("id") + " não encontrado" ))
                    .otherwise()
                        .log("Pedido encontrado: ${body}")
                        .marshal()
                            .json()
                .end()
                .setHeader("Content-Type", constant("application/json"));
    }
}