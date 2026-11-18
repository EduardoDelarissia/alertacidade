package br.com.fiap.alertacidade.exception;

/** Requisicao bem formada que viola uma regra de negocio -> HTTP 422. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
