package com.koukakios.contextpilot;

public interface AiClient {
    /**
     * Sends a formatted prompt to the AI provider and returns the raw text response.
     * * @param prompt The complete, finalized prompt string.
     * @return The AI's response text.
     * @throws Exception If the network fails, the API key is missing, or the JSON fails to parse.
     */
    String askAi(String prompt) throws Exception;
}