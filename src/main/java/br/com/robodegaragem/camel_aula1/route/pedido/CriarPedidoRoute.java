package br.com.robodegaragem.camel_aula1.route.pedido;

import br.com.robodegaragem.camel_aula1.generated.model.NovoPedido;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CriarPedidoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:criarPedido")
                .routeId("criar-pedido")
                .log("JSON recebido: ${body}")
                .unmarshal()
                    .json(NovoPedido.class)
                .log("Objeto NovoPedido: ${body}")
                .to("sql:classpath:sql/inserir-pedido.sql")
                .setBody(constant("{\"mensagem\":\"Pedido inserido com sucesso\"}"))
                .setHeader("Content-Type", constant("application/json"));
    }
}