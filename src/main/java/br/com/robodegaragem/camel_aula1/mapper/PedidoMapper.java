package br.com.robodegaragem.camel_aula1.mapper;

import br.com.robodegaragem.camel_aula1.generated.model.Pedido;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PedidoMapper {

    private final ObjectMapper objectMapper;

    public PedidoMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Pedido fromGeneratedKeys(List<Map<String, Object>> rows) {

        if (rows == null || rows.isEmpty()) {
            throw new IllegalStateException(
                    "O banco não retornou o pedido criado"
            );
        }

        return objectMapper.convertValue(rows.get(0), Pedido.class);
    }
}