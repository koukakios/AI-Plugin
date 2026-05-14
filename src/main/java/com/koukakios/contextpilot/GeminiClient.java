package com.koukakios.contextpilot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GeminiClient implements AiClient {

    // We use the lightning-fast Flash model for IDEs
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    @Override
    public String askAi(String prompt) throws Exception {
        // 1. Grab the API key from your computer's environment variables
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("GEMINI_API_KEY environment variable is missing!");
        }

        // 2. Sanitize the prompt for JSON (escape quotes and newlines)
        String sanitizedPrompt = prompt.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        // 3. Build the exact JSON payload Google expects
        String jsonPayload = """
            {
              "contents": [{
                "parts": [{"text": "%s"}]
              }]
            }
            """.formatted(sanitizedPrompt);

        // 4. Set up the Java HTTP Client
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // 5. Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API returned error code: " + response.statusCode() + "\n" + response.body());
        }

        // 6. Extract the text from the response (Simple parsing to avoid extra dependencies)
        return extractTextFromJson(response.body());
    }

    // A lightweight helper to pull the text out of Google's JSON response
    private String extractTextFromJson(String json) throws Exception {
        String searchKey = "\"text\": \"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            throw new Exception("Could not parse AI response.");
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);

        // Handle potential escaped quotes inside the AI's response
        while (endIndex != -1 && json.charAt(endIndex - 1) == '\\') {
            endIndex = json.indexOf("\"", endIndex + 1);
        }

        if (endIndex == -1) {
            throw new Exception("Could not parse AI response.");
        }

        String rawText = json.substring(startIndex, endIndex);
        // Unescape newlines and quotes so it looks normal in your IDE
        return rawText.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }
}