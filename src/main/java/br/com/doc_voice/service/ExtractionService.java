package br.com.doc_voice.service;

import br.com.doc_voice.utils.FileValidationUtils;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.spire.presentation.FileFormat;
import com.spire.presentation.Presentation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExtractionService {

    @Value("${google.api.key}")
    private String apiKey;

    private Client client;

    @PostConstruct
    public void init() {
        this.client = new Client.Builder().apiKey(apiKey).build();
    }

    String pdfMimeType = "application/pdf";

    private static final String ACCESSIBILITY_PROMPT =
            """
                    Você é um assistente especializado em criar resumos acessíveis e claros de documentos para pessoas com deficiência visual, dislexia ou dificuldades de leitura.
                    \s
                    Por favor, analise o seguinte conteúdo e crie um resumo acessível seguindo estas diretrizes:
                                  \s
                                    1. **Linguagem Clara**: Use frases simples e diretas
                                    2. **Estrutura Organizada**: Organize as informações em tópicos principais
                                    3. **Acessibilidade**: Descreva qualquer referência a elementos visuais (gráficos, tabelas, imagens)
                                    4. **Concisão**: Mantenha as informações essenciais, removendo redundâncias
                                    5. **Contexto**: Forneça contexto suficiente para compreensão autônoma
                                  \s
                                    **Formato de Resposta:**
                                    - Comece com um parágrafo de contextualização
                                    - Liste os pontos principais em tópicos numerados
                                    - Inclua uma conclusão ou síntese final
                                    - Se houver elementos visuais mencionados, descreva-os textualmente
                                 \s
                    Responda somente o resumo acessível, sem cumprimentos ou floreios.
                    
                    """;

    public String extractText(MultipartFile file) throws Exception {

        FileValidationUtils.ValidationResult fileValidationResult = FileValidationUtils.validate(file);
        if (!fileValidationResult.isValid()) return fileValidationResult.getMessage();

        String contentType = file.getContentType();

        Content content = switch (contentType) {
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
                byte[] converted = convertPptToPdf(file);
                yield Content.fromParts(
                        Part.fromBytes(converted, pdfMimeType),
                        Part.fromText(ACCESSIBILITY_PROMPT + "Conteúdo do documento: ")
                );
            }
            case "application/pdf" -> {
                String text = extractFromPdf(file);
                yield Content.fromParts(Part.fromText(ACCESSIBILITY_PROMPT + "Conteúdo do documento PDF:\n\n " + text));
            }
            case "image/jpeg", "image/jpg", "image/png" -> {
                byte[] imageBytes = file.getBytes();
                yield Content.fromParts(
                        Part.fromText(ACCESSIBILITY_PROMPT + "Conteúdo da imagem:"),
                        Part.fromBytes(imageBytes, contentType)
                );
            }
            case null -> throw new IllegalArgumentException("Tipo de arquivo não identificado");
            default -> throw new IllegalArgumentException("Tipo de arquivo não suportado: " + contentType);
        };

        return callGeminiApi(content);
    }

    private String extractFromPdf(MultipartFile pdfFile) throws IOException {
        try (RandomAccessRead randomAccessRead = new RandomAccessReadBuffer(pdfFile.getInputStream())) {
            PDDocument document = Loader.loadPDF(randomAccessRead);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String callGeminiApi(Content content) {
        GenerateContentResponse response = client.models
                .generateContent("gemini-2.5-flash", content, null);
        if (response != null && response.text() != null) {
            return response.text();
        } else {
            return "Não foi possível simplificar o texto. Resposta da API Gemini vazia.";
        }
    }

    private byte[] convertPptToPdf(MultipartFile file) throws Exception {
        Presentation ppt = new Presentation();
        ppt.loadFromStream(file.getInputStream(), FileFormat.PPT);

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        ppt.saveToFile(pdfOutputStream, FileFormat.PDF);
        byte[] bytes = pdfOutputStream.toByteArray();

        ppt.dispose();
        return bytes;
    }
}
