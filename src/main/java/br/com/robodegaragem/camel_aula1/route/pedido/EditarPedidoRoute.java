package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.generated.model.NovoPedido;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EditarPedidoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:editarPedido")
                .routeId("editar-pedido")
                .log("Editando pedido ID: ${header.id}")
                .convertHeaderTo("id", Long.class)
                .unmarshal()
                    .json(NovoPedido.class)
                .log("Novos dados do pedido: ${body}")
                .to("sql:classpath:sql/editar-pedido.sql")
                .log("Quantidade de registros atualizados: ${header.CamelSqlUpdateCount}")
                .choice()
                    .when(header("CamelSqlUpdateCount").isEqualTo(0))
                        .log("Pedido não encontrado: ${header.id}")
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                        .setBody(constant("{\"mensagem\":\"Pedido não encontrado\"}"))
                    .otherwise()
                        .log("Pedido atualizado com sucesso: ${header.id}")
                        .to("sql:classpath:sql/buscar-pedido.sql?outputType=SelectOne")
                        .marshal().json()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .end()
                .setHeader("Content-Type", constant("application/json"));
    }
}