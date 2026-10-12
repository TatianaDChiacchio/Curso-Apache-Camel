package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.model.ErrorResponseProcessor;
import br.com.robodegaragem.camel_aula1.processor.CorrelationLogProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExcluirPedidoRoute extends RouteBuilder {

    private final ErrorResponseProcessor errorResponseProcessor;
    private final CorrelationLogProcessor correlationLogProcessor;

    public ExcluirPedidoRoute(ErrorResponseProcessor errorResponseProcessor, CorrelationLogProcessor correlationLogProcessor) {
        this.errorResponseProcessor = errorResponseProcessor;
        this.correlationLogProcessor = correlationLogProcessor;
    }

    @Override
    public void configure() throws Exception {

        from("direct:excluirPedido")
                .routeId("excluir-pedido")
                .process(correlationLogProcessor)
                .log("Excluindo pedido ID: ${header.id}")
                .convertHeaderTo("id", Long.class)
                .to("sql:classpath:sql/excluir-pedido.sql")
                .log("Quantidade de registros excluídos: ${header.CamelSqlUpdateCount}")
                .choice()
                    .when(header("CamelSqlUpdateCount").isEqualTo(0))
                    .log("Pedido não encontrado: ${header.id}")
                    .process(exchange -> errorResponseProcessor.setError(
                        exchange, 404, "Pedido Not Found", "Pedido " + exchange.getMessage().getHeader("id") + " não encontrado" ))
                .otherwise()
                    .log("Pedido excluído com sucesso: ${header.id}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                    .setBody(constant(null))
                .end();
    }
}