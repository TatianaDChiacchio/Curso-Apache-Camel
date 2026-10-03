package br.com.robodegaragem.camel_aula1.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;

//@Component
public class CsvToJsonRoute extends RouteBuilder {

    private static final String ROUTE_ID = "csv-to-json-route";

    @Override
    public void configure() throws Exception {

        CsvDataFormat csv = new CsvDataFormat();
        csv.setUseMaps(true);
        csv.setUseOrderedMaps(true);

        from("file:input?noop=true&include=.*\\.csv")
                .routeId(ROUTE_ID)
                .log("Arquivo encontrado: ${header.CamelFileName}")
                .unmarshal(csv)
                .log("CSV convertido: ${body}")
                .split(body())
                   // .log("Processando pedido: ${body}")
                    .setHeader("orderPrice", simple("${body[price]}"))
                    .marshal().json()
                    .log("Pedido convertido para JSON: ${body}")
                    .process(exchange -> {
                        String body = exchange.getMessage().getBody(String.class);
                        exchange.getMessage().setBody(body + System.lineSeparator());
                    })
                    .choice()
                        .when(simple("${header.orderPrice} > 100"))
                        .log("Enviando pedido para PRIORITY")
                        .to("file:output/priority?fileName=orders.jsonl&fileExist=Append")
                    .otherwise()
                        .log("Enviando pedido para STANDARD")
                        .to("file:output/standard?fileName=orders.jsonl&fileExist=Append")
                    .end()
                .end();
    }
}