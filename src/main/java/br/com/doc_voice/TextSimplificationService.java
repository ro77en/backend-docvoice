package br.com.doc_voice;

import org.springframework.stereotype.Service;

@Service
public class TextSimplificationService {

  /**
   * Este método recebe um texto original e retorna uma versão simplificada.
   * No momento, ele está com uma resposta simulada ("mock") que será substituída por chamada real à API.
   */
  public String simplificarTexto(String original) {
    // Aqui você pode montar o prompt de entrada para a IA
    String prompt = "Simplifique o seguinte texto, mantendo o sentido e tornando-o mais fácil de entender:\n" + original;

    // 🔧 FUTURAMENTE: usar HttpClient ou RestTemplate para fazer chamada real à API da IA (ex: DeepSeek ou OpenAI)

    // Simulando a resposta da IA para testes iniciais:
    return "Texto simplificado (mock)";
  }
}
