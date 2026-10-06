package br.com.robodegaragem.camel_aula1.route.order;

import br.com.robodegaragem.camel_aula1.processor.XmlOrderProcessor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlOrderIngestionRouteTest extends CamelTestSupport {

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new XmlOrderIngestionRoute(new XmlOrderProcessor());
    }

    @Test
    void deveTransformarXmlEmJson() throws Exception {

        // Altera a rota somente durante o teste
        AdviceWith.adviceWith(
                context,
                "xml-order-ingestion-route",
                advice -> {

                    // Substitui o consumidor de arquivos por um endpoint controlado
                    // pelo próprio teste.
                    advice.replaceFromWith("direct:start");

                    // Substitui a gravação real em data/outbox por um MockEndpoint.
                    advice.weaveByToUri("file:data/outbox")
                            .replace()
                            .to("mock:outbox");
                }
        );

        // Como usamos isUseAdviceWith() = true,
        // precisamos iniciar o CamelContext manualmente.
        context.start();

        MockEndpoint mockOutbox = getMockEndpoint("mock:outbox");

        // Esperamos exatamente uma mensagem na saída.
        mockOutbox.expectedMessageCount(1);

        // Além da quantidade, verificamos o conteúdo da mensagem.
        mockOutbox.expectedMessagesMatches(exchange -> {

            String body = exchange.getMessage().getBody(String.class);

            return body.contains("\"orderId\" : \"1001\"")
                    && body.contains("\"customer\" : \"GlobalTech Ltda\"")
                    && body.contains("\"product\" : \"Notebook\"")
                    && body.contains("\"quantity\" : \"2\"")
                    && body.contains("\"price\" : \"4500.00\"");
        });

        String xml = """
                <order>
                    <orderId>1001</orderId>
                    <customer>GlobalTech Ltda</customer>
                    <product>Notebook</product>
                    <quantity>2</quantity>
                    <price>4500.00</price>
                </order>
                """;

        // Envia a mensagem para a rota.
        template.sendBody("direct:start", xml);

        // Verifica se todas as expectativas acima foram satisfeitas.
        mockOutbox.assertIsSatisfied();
    }

    @Test
    void deveEnviarXmlInvalidoParaDeadletter() throws Exception {

        AdviceWith.adviceWith(
                context,
                "xml-order-ingestion-route",
                advice -> {

                    // Substitui a entrada real por uma entrada controlada pelo teste
                    advice.replaceFromWith("direct:start");

                    // Substitui a saída normal por um MockEndpoint
                    advice.weaveByToUri("file:data/outbox")
                            .replace()
                            .to("mock:outbox");

                    // Substitui o deadletter real por um MockEndpoint
                    advice.weaveByToUri("file:data/deadletter")
                            .replace()
                            .to("mock:deadletter");
                }
        );

        context.start();

        MockEndpoint mockOutbox = getMockEndpoint("mock:outbox");
        MockEndpoint mockDeadletter = getMockEndpoint("mock:deadletter");

        // O XML inválido NÃO pode chegar à saída normal
        mockOutbox.expectedMessageCount(0);

        // O XML inválido DEVE chegar ao deadletter
        mockDeadletter.expectedMessageCount(1);

        // Além de chegar ao deadletter, queremos comprovar que
        // useOriginalMessage() preservou o conteúdo XML original.
        mockDeadletter.expectedMessagesMatches(exchange -> {

            String body = exchange.getMessage().getBody(String.class);

            return body.contains("<orderId>9999</orderId>")
                    && body.contains("<customer>Cliente Inválido</customer>")
                    && body.contains("<product>Monitor</product>")
                    && body.contains("<quantity>2</quantity>")
                    && body.contains("<price>1500.00</price>");
        });

        String xmlInvalido = """
            <order>
                <orderId>9999</orderId>
                <customer>Cliente Inválido</customer>
                <product>Monitor</product>
                <quantity>2</quantity>
                <price>1500.00</price>
            """;

        // Envia o XML malformado para a rota
        template.sendBody("direct:start", xmlInvalido);

        // Verifica todas as expectativas dos MockEndpoints
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void deveRealizarRedeliveryEmCasoDeIOException() throws Exception {

        AtomicInteger tentativas = new AtomicInteger(0);

        AdviceWith.adviceWith(
                context,
                "xml-order-ingestion-route",
                advice -> {

                    advice.replaceFromWith("direct:start");

                    advice.weaveByToUri("file:data/outbox")
                            .replace()
                            .process(exchange -> {

                                int tentativaAtual = tentativas.incrementAndGet();

                                System.out.println(
                                        "Tentativa de gravação: " + tentativaAtual
                                );

                                throw new IOException(
                                        "Falha simulada de I/O"
                                );
                            });

                    advice.weaveByToUri("file:data/deadletter")
                            .replace()
                            .to("mock:deadletter");
                }
        );

        context.start();

        MockEndpoint mockDeadletter =
                getMockEndpoint("mock:deadletter");

        mockDeadletter.expectedMessageCount(1);

        String xml = """
            <order>
                <orderId>2001</orderId>
                <customer>Cliente Redelivery</customer>
                <product>Teclado</product>
                <quantity>1</quantity>
                <price>250.00</price>
            </order>
            """;

        template.sendBody("direct:start", xml);

        mockDeadletter.assertIsSatisfied();

        assertEquals(4, tentativas.get());
    }
}