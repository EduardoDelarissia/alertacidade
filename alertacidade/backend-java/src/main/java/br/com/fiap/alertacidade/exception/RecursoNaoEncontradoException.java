package br.com.fiap.alertacidade.exception;

/** Recurso inexistente -> HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " nao encontrado");
    }
}
