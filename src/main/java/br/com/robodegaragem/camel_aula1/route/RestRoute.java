package br.com.robodegaragem.camel_aula1.route;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class RestRoute extends RouteBuilder {

    private static final String ROUTE_ID = "simpleRoute";

    @Override
    public void configure() {

        from( "rest:get:input" )
                .routeId( ROUTE_ID )
                .log( "Received request with input: ${header.name}" )
                .transform()
                .simple( "Hello, ${header.name}! Welcome to Apache Camel with Spring Boot." )
                .log( "Response: ${body}" );

    }
}