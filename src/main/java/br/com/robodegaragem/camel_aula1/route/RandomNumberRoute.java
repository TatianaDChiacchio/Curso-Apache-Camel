package br.com.robodegaragem.camel_aula1.route;

import br.com.robodegaragem.camel_aula1.exception.OverNumberException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.ThreadLocalRandom;

//@Component
public class RandomNumberRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Tratamento genérico
        onException(Exception.class)
                .handled(true)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        System.out.println(
                                "Need to handle exception here, got data: "
                                        + exchange.getIn().getBody()
                        );
                    }
                });


        // Tratamento específico
        onException(OverNumberException.class)
                .handled(true)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        System.out.println(
                                "Get number: "
                                        + exchange.getIn().getBody()
                                        + ", the number is greater than 20."
                        );
                    }
                });


        from("timer://trigger?period=10s")
                .routeId("timer-random-route")
                .to("direct://start");


        from("direct://start")
                .routeId("generate-random-route")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        int i = getRandomNumberInRange(1, 100);

                        System.out.println("Número gerado: " + i);

                        if (i > 50) {
                            exchange.getIn().setBody("a");
                        } else {
                            exchange.getIn().setBody(i);
                        }
                    }
                })
                .to("direct://doSomething");


        from("direct://doSomething")
                .routeId("do-something-route")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        int number =
                                exchange.getIn().getBody(Integer.class);

                        System.out.println("Got number : " + number);

                        if (number > 20) {
                            throw new OverNumberException(
                                    "number is over 20"
                            );
                        }
                    }
                });
    }


    private int getRandomNumberInRange(int min, int max) {

        return ThreadLocalRandom.current()
                .nextInt(min, max + 1);
    }
}