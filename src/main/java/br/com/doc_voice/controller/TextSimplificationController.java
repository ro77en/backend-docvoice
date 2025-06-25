package br.com.doc_voice.controller;


import br.com.doc_voice.TextSimplificationService;
import br.com.doc_voice.dto.TextSimplificationRequestDTO;
import br.com.doc_voice.dto.TextSimplificationResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simplify")
public class TextSimplificationController {

  private final TextSimplificationService service;

  // Injeção de dependência do service via construtor
  public TextSimplificationController(TextSimplificationService service) {
    this.service = service;
  }

  /**
   * Endpoint POST que recebe um texto e retorna sua versão simplificada.
   * URL: /api/simplify
   * Corpo da requisição: { "textoOriginal": "..." }
   * Corpo da resposta: { "textoOriginal": "...", "textoSimplificado": "..." }
   */
  @PostMapping
  public ResponseEntity<TextSimplificationResponseDTO> simplificar(@RequestBody TextSimplificationRequestDTO dto) {
    // Chama o service para realizar a simplificação
    String textoSimplificado = service.simplificarTexto(dto.textoOriginal());

    // Retorna um DTO com o original e o simplificado
    return ResponseEntity.ok(new TextSimplificationResponseDTO(dto.textoOriginal(), textoSimplificado));
  }
}
