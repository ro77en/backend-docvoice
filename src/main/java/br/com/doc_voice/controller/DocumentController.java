package br.com.doc_voice.controller;

import br.com.doc_voice.dto.TextExtractionResponseDTO;
import br.com.doc_voice.exception.ConvertException;
import br.com.doc_voice.exception.ExtractionException;
import br.com.doc_voice.exception.GeminiCallException;
import br.com.doc_voice.exception.InvalidFileException;
import br.com.doc_voice.service.ExtractionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final ExtractionService extractionService;

    public DocumentController(ExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    @PostMapping("/extract-text")
    public ResponseEntity<?> extractText(@Valid @RequestParam("file") MultipartFile file) {
        try {
            String text = extractionService.extractText(file);
            return ResponseEntity.ok(new TextExtractionResponseDTO(text));
        } catch (InvalidFileException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ExtractionException e) {
            if (e.getMessage().contains("Erro na extração de texto")) {
                return ResponseEntity.badRequest().body("Arquivo PDF corrompido ou danificado");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ConvertException e) {
            return ResponseEntity.badRequest().body("Arquivo PowerPoint corrompido ou danificado");
        } catch (GeminiCallException e) {
            return ResponseEntity.status(500).body("Erro no processamento do texto");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }
}
