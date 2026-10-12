package br.com.robodegaragem.camel_aula1.model;

import br.com.robodegaragem.camel_aula1.generated.model.ErroPedido;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseProcessor {

    private final ObjectMapper objectMapper;

    public ErrorResponseProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setError(
            Exchange exchange,
            int status,
            String erro,
            String mensagem
    ) throws Exception {

        ErroPedido response = new ErroPedido();
        response.setErro(erro);
        response.setMensagem(mensagem);

        exchange.getMessage().setHeader(
                Exchange.HTTP_RESPONSE_CODE, status
        );

        exchange.getMessage().setHeader(
                Exchange.CONTENT_TYPE, "application/json"
        );

        exchange.getMessage().setBody(
                objectMapper.writeValueAsString(response)
        );
    }
}