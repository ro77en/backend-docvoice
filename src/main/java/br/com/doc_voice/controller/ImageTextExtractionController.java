package br.com.doc_voice.controller;

import br.com.doc_voice.service.ImageTextExtractionService;
import br.com.doc_voice.utils.FileValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
public class ImageTextExtractionController {

    @Autowired
    private ImageTextExtractionService imageTextExtractionService;

    @PostMapping("/extract-text")
    public ResponseEntity<String> extractTextFromImage(@RequestParam("file") MultipartFile file) {
        FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
        try {
            String text = imageTextExtractionService.extractText(file);
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao extrair texto da imagem: " + e.getMessage());
        }
    }
}