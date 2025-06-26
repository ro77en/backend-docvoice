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

    public String extractText(MultipartFile file) throws Exception {

        FileValidationUtils.ValidationResult fileValidationResult = FileValidationUtils.validate(file);
        if (!fileValidationResult.isValid()) return fileValidationResult.getMessage();

        Content content = switch (file.getContentType()) {
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
                byte[] converted = convertPptToPdf(file);
                yield Content.fromParts(Part.fromBytes(converted, pdfMimeType),
                        Part.fromText("Descreva a imagem acima e extraia o conteúdo de texto, se houver."));
            }
            case "application/pdf" -> {
                String text = extractFromPdf(file);
                yield Content.fromParts(Part.fromText(text));
            }
            case null -> null;
            default -> throw new IllegalArgumentException("Tipo de arquivo não suportado: " + file.getContentType());
        };

        return callGeminiApi(content);
    }

    private String extractFromPdf(MultipartFile pdfFile) throws IOException {
        try (RandomAccessRead randomAccessRead = new RandomAccessReadBuffer(pdfFile.getInputStream())) {
            PDDocument document = Loader.loadPDF(randomAccessRead);
            PDFTextStripper stripper = new PDFTextStripper();
            String response = simplifyText((stripper.getText(document)));
            return response;
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

    public String simplifyText(String text) {
        String prompt = "Você é um assistente especializado em criar resumos acessíveis e claros de documentos para pessoas com deficiência visual, dislexia ou dificuldades de leitura.\n" +
                " \n" +
                "Por favor, analise o seguinte texto extraído de um documento e crie um resumo acessível seguindo estas diretrizes:\n" +
                "               \n" +
                "                1. **Linguagem Clara**: Use frases simples e diretas\n" +
                "                2. **Estrutura Organizada**: Organize as informações em tópicos principais\n" +
                "                3. **Acessibilidade**: Descreva qualquer referência a elementos visuais (gráficos, tabelas, imagens)\n" +
                "                4. **Concisão**: Mantenha as informações essenciais, removendo redundâncias\n" +
                "                5. **Contexto**: Forneça contexto suficiente para compreensão autônoma\n" +
                "               \n" +
                "                **Formato de Resposta:**\n" +
                "                - Comece com um parágrafo de contextualização\n" +
                "                - Liste os pontos principais em tópicos numerados\n" +
                "                - Inclua uma conclusão ou síntese final\n" +
                "                - Se houver elementos visuais mencionados, descreva-os textualmente\n" +
                "              \n" +
                "Responda somente o resumo acessível, sem cumprimentos ou floreios." +
                " " +
                "\n\n" + text;

        Content content = Content.fromParts(Part.fromText(prompt));
        return callGeminiApi(content);
    }
}
