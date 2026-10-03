package br.com.robodegaragem.camel_aula1.exception;

public class OverNumberException extends RuntimeException {

    public OverNumberException(String message) {
        super(message);
    }
}