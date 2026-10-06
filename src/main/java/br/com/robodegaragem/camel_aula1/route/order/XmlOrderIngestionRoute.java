package br.com.robodegaragem.camel_aula1.route.order;

import br.com.robodegaragem.camel_aula1.processor.XmlOrderProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class XmlOrderIngestionRoute extends RouteBuilder {

    private final XmlOrderProcessor xmlOrderProcessor;

    public XmlOrderIngestionRoute(XmlOrderProcessor xmlOrderProcessor) {
        this.xmlOrderProcessor = xmlOrderProcessor;
    }

    @Override
    public void configure() throws Exception {

        onException(IOException.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(2000)
                .useOriginalMessage()
                .handled(true)
                .log("Falha de I/O após as tentativas de redelivery: ${exception.message}")
                .setHeader(
                        Exchange.FILE_NAME,
                        simple("${file:name}")
                )
                .to("file:data/deadletter");

        onException(JsonProcessingException.class)
                .useOriginalMessage()
                .handled(true)
                .log("Erro ao processar XML ${header.CamelFileName}: ${exception.message}")
                .setHeader(
                        Exchange.FILE_NAME,
                        simple("${file:name}")
                )
                .to("file:data/deadletter");

        from("file:data/inbox?noop=true&include=.*\\.xml")
                .routeId("xml-order-ingestion-route")
                .log("Arquivo recebido: ${header.CamelFileName}")
                .process(xmlOrderProcessor)
                .log("XML transformado para JSON: ${body}")
                .setHeader(
                        Exchange.FILE_NAME,
                        simple("${file:name.noext}.json")
                )
                .to("file:data/outbox")
                .log("Pedido ${exchangeProperty.orderId} do arquivo ${header.CamelFileName} transformado para JSON e gravado na saída.");
    }
}