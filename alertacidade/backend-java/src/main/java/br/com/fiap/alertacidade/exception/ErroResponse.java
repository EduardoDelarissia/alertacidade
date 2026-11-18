package br.com.fiap.alertacidade.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Formato padrao de erro devolvido pela API. */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {
}
