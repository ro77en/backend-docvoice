package br.com.doc_voice.dto;

/**
 * DTO da requisição: recebe o texto original que será simplificado.
 * Exemplo de entrada JSON:
 * {
 *   "textoOriginal": "A legislação vigente estabelece diretrizes..."
 * }
 */
public record TextSimplificationRequestDTO(String textoOriginal) {
}
