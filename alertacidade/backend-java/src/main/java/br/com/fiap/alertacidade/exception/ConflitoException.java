package br.com.fiap.alertacidade.exception;

/** Conflito com o estado atual dos dados (duplicidade, registros dependentes) -> HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
