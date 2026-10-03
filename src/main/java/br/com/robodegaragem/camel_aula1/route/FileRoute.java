package br.com.robodegaragem.camel_aula1.route;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class FileRoute extends RouteBuilder {

    private static final String INPUT = "file://input?noop=true";
    private static final String OUTPUT = "file://output";

    @Override
    public void configure() {
        from( INPUT)
                .log("Processando arquivo: ${header.CamelFileName}")
                .process(exchange -> {
                    String conteudo = exchange.getIn().getBody(String.class);
                    conteudo = conteudo + " Processed by Apache Camel";
                    exchange.getIn().setBody(conteudo);
                })
                .log( "Conteúdo modificado: ${body}" )
                .to( OUTPUT );

    }
}
