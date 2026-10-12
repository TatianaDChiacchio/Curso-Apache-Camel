package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.generated.model.NovoPedido;
import br.com.robodegaragem.camel_aula1.mapper.PedidoMapper;
import br.com.robodegaragem.camel_aula1.model.ErrorResponseProcessor;
import br.com.robodegaragem.camel_aula1.processor.CorrelationLogProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CriarPedidoRoute extends RouteBuilder {

    private final ErrorResponseProcessor errorResponseProcessor;
    private final CorrelationLogProcessor correlationLogProcessor;

    public CriarPedidoRoute(ErrorResponseProcessor errorResponseProcessor, CorrelationLogProcessor correlationLogProcessor) {
        this.errorResponseProcessor = errorResponseProcessor;
        this. correlationLogProcessor = correlationLogProcessor;
    }

    @Override
    public void configure() throws Exception {

        from("direct:criarPedido")
                .routeId("criar-pedido")
                .process(correlationLogProcessor)
                .log("JSON recebido: ${body}")
                .unmarshal()
                    .json(NovoPedido.class)
                .choice()
                .when(simple("${body.getValor()} >= 5000"))
                        .log("Pedido de ALTO VALOR identificado: ${body}")
                        .process(exchange -> errorResponseProcessor.setError(
                            exchange, 422, "PEDIDO_ALTO_VALOR", "Pedido não criado. O valor informado excede o limite permitido de R$ 5.000,00." ))
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

    }
}