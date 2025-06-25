package br.com.doc_voice.dto;


/**
 * Este DTO representa a resposta que o backend devolverá para o frontend.
 * Ele inclui tanto o texto original quanto a versão simplificada.
 */
public record TextSimplificationResponseDTO(String textoOriginal, String textoSimplificado) {
}
