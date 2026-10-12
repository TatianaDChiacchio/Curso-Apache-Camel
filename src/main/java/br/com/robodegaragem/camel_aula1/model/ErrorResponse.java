package br.com.robodegaragem.camel_aula1.model;

public record ErrorResponse(
        String erro,
        String mensagem
) {
}