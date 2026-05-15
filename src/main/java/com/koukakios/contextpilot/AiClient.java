package com.koukakios.contextpilot;

/**
 * Defines a client capable of sending prompts to an AI provider.
 */
public interface AiClient {
    /**
     * Sends a formatted prompt to the AI provider and returns the raw text response.
     *
     * @param prompt complete, finalized prompt string
     * @return AI response text
     * @throws Exception if the request fails, credentials are missing, or the response cannot be parsed
     */
    String askAi(String prompt) throws Exception;
}
