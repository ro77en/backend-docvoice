package br.com.doc_voice.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationUtilsTest {

    @Test
    void testValidJpegFile() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("image/jpeg");
        Mockito.when(file.getOriginalFilename()).thenReturn("foto.jpg");
        Mockito.when(file.getSize()).thenReturn(1024L);
        Mockito.when(file.isEmpty()).thenReturn(false);

        FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
        assertTrue(result.isValid());
        assertEquals("Arquivo válido.", result.getMessage());
    }

@Test
void testInvalidFormat() {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    Mockito.when(file.getContentType()).thenReturn("text/plain");
    Mockito.when(file.getOriginalFilename()).thenReturn("notas.txt");
    Mockito.when(file.getSize()).thenReturn(1024L);
    Mockito.when(file.isEmpty()).thenReturn(false);

    FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
    assertFalse(result.isValid());
    assertTrue(result.getMessage().startsWith("Formato text/plain não é suportado."));
    assertTrue(result.getMessage().contains("image/jpeg"));
    assertTrue(result.getMessage().contains("application/pdf"));
}
@Test
void testInvalidExtension() {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    Mockito.when(file.getContentType()).thenReturn("image/png");
    Mockito.when(file.getOriginalFilename()).thenReturn("foto.gif");
    Mockito.when(file.getSize()).thenReturn(1024L);
    Mockito.when(file.isEmpty()).thenReturn(false);

    FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
    assertFalse(result.isValid());
    assertEquals("Extensão inválida.", result.getMessage());
}

@Test
void testFileTooLarge() {
    MultipartFile file = Mockito.mock(MultipartFile.class);
    Mockito.when(file.getContentType()).thenReturn("application/pdf");
    Mockito.when(file.getOriginalFilename()).thenReturn("large.pdf");
    Mockito.when(file.getSize()).thenReturn(6 * 1024 * 1024L);
    Mockito.when(file.isEmpty()).thenReturn(false);

    FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
    assertFalse(result.isValid());
    assertEquals("Arquivo muito grande (5 MB).", result.getMessage()); 
}

    @Test
    void testEmptyFile() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(true);

        FileValidationUtils.ValidationResult result = FileValidationUtils.validate(file);
        assertFalse(result.isValid());
        assertEquals("Arquivo vazio.", result.getMessage());
    }

    @Test
    void testNullFile() {
        FileValidationUtils.ValidationResult result = FileValidationUtils.validate(null);
        assertFalse(result.isValid());
        assertEquals("Arquivo vazio.", result.getMessage());
    }
}