package br.com.doc_voice.controller;

import br.com.doc_voice.dto.TextExtractionResponseDTO;
import br.com.doc_voice.service.ExtractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final ExtractionService extractionService;

    @PostMapping("/extract-text")
    public ResponseEntity<TextExtractionResponseDTO> extractText(
            @Valid @RequestParam("file") MultipartFile file) throws Exception {
        String text = extractionService.extractText(file);
        return ResponseEntity.status(HttpStatus.OK).body(new TextExtractionResponseDTO(text));
    }
}
