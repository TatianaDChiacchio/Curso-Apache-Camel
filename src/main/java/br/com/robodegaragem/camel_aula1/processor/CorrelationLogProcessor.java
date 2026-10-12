
package br.com.robodegaragem.camel_aula1.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CorrelationLogProcessor implements Processor {

    private static final Logger log =
            LoggerFactory.getLogger(CorrelationLogProcessor.class);

    @Override
    public void process(Exchange exchange) {

        String correlationId = exchange.getMessage()
                .getHeader("X-Correlation-ID", String.class);

        log.info(
                "BACKEND PEDIDOS | Metodo={} | ExchangeId={}",
                exchange.getMessage().getHeader(
                        Exchange.HTTP_METHOD, String.class
                ),
                exchange.getExchangeId()
        );
    }
}
