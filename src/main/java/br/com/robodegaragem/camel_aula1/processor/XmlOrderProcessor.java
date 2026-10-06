package br.com.robodegaragem.camel_aula1.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class XmlOrderProcessor implements Processor {

    // XmlMapper é o componente do Jackson especializado em XML.
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;

    public XmlOrderProcessor() {
        this.xmlMapper = new XmlMapper();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        // "Camel, me entregue o Body desta mensagem como String."
        // o JACKSON lê o XML e cria uma representação em ávore, onde cada campo é um e dentro dele o seu valor
        String xml = exchange.getMessage().getBody(String.class);
        // O JsonNode é uma estrutura intermediária em memória.
        //Ainda não é necessariamente a String JSON que vamos gravar no arquivo.
        JsonNode jsonNode = xmlMapper.readTree(xml);

        String orderId = jsonNode.path("orderId").asText();
        exchange.setProperty("orderId", orderId);

        // O ObjectMapper pega aquela árvore e a serializa como JSON.
        // é apenas para deixar o JSON formatado e legível.
        String json = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonNode);

        // aqui substituímos o Body
        exchange.getMessage().setBody(json);

        // O mesmo Exchange continua percorrendo a rota, mas seu Body foi transformado.
    }
}