package br.com.doc_voice.utils;

import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;

public class FileValidationUtils {

    private static final String[] ALLOWED_FORMATS = {
        "image/jpeg",
        "image/png",
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    };

    private static final String[] ALLOWED_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".pdf", ".pptx"
    };

    private static final long MAX_SIZE = 5 * 1024 * 1024; // conferir com a squad

    public static boolean isValidFormat(MultipartFile file) {
        return Arrays.asList(ALLOWED_FORMATS).contains(file.getContentType());
    }

    public static boolean isValidSize(MultipartFile file) {
        return file.getSize() <= MAX_SIZE;
    }

    public static boolean isValidExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) return false;
        return Arrays.stream(ALLOWED_EXTENSIONS).anyMatch(filename.toLowerCase()::endsWith);
    }

    public static ValidationResult validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ValidationResult(false, "Arquivo vazio.");
        }
        if (!isValidFormat(file)) {
            return new ValidationResult(false,"Formato " + file.getContentType() + " não é suportado. Use: " + String.join(", ", ALLOWED_FORMATS));
        }
        if (!isValidSize(file)) {
            return new ValidationResult(false, "Arquivo muito grande (" + MAX_SIZE / (1024 * 1024) + " MB).");
        }
        if (!isValidExtension(file)) {
            return new ValidationResult(false, "Extensão inválida.");
        }
        return new ValidationResult(true, "Arquivo válido.");
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}