package br.com.doc_voice.controller;

import br.com.doc_voice.utils.FileValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileValidationController {

    @PostMapping("/validate")
    public ResponseEntity<?> validateFile(@RequestParam("file") MultipartFile file) {
        FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
        
        if (result.isValid()) {
            return ResponseEntity.ok().body(result.getMessage());
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }
}