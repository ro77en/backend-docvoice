package br.com.doc_voice.exception;

public class GeminiCallException extends RuntimeException {
    public GeminiCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeminiCallException(String message) {
        super(message);
    }
}
