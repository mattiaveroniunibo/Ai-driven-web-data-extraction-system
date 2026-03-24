package com.kayak.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class McpClientService {

    private static final Logger LOG = Logger.getLogger(McpClientService.class);

    private static final String IMAGE_SERVER_URL = "http://localhost:8082/mcp/execute";
    private static final String NEWS_SERVER_URL = "http://localhost:8081/mcp/execute";

    public String downloadImage(String imageUrl) {
        LOG.info("Calling IMAGE MCP server for: " + imageUrl);

        try {
            // Prepara JSON request
            String jsonRequest = "{\"tool\":\"downloadImage\",\"url\":\"" + imageUrl + "\"}";

            // Chiama server MCP
            String response = callMcpServer(IMAGE_SERVER_URL, jsonRequest);

            // Estrae risultato dal JSON
            String result = extractResult(response);
            LOG.info("IMAGE MCP server response: " + result);

            return result;
        } catch (Exception e) {
            LOG.error("Error calling IMAGE server", e);
            return "ERROR: " + e.getMessage();
        }
    }

    public String readNews(String newsUrl) {
        LOG.info("Calling NEWS MCP server for: " + newsUrl);

        try {
            // Prepara JSON request
            String jsonRequest = "{\"tool\":\"readNews\",\"url\":\"" + newsUrl + "\"}";

            // Chiama server MCP
            String response = callMcpServer(NEWS_SERVER_URL, jsonRequest);

            // Estrae risultato dal JSON
            String result = extractResult(response);
            LOG.info("NEWS MCP server response: " + result);

            return result;
        } catch (Exception e) {
            LOG.error("Error calling NEWS server", e);
            return "ERROR: " + e.getMessage();
        }
    }

    private String callMcpServer(String serverUrl, String jsonRequest) throws Exception {
        URL url = new URL(serverUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Configura POST
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Invia JSON
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Leggi risposta
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        return response.toString();
    }

    private String extractResult(String jsonResponse) {
        // Semplice parsing JSON per estrarre "result"
        // Cerca: {"result":"..."}
        int startIndex = jsonResponse.indexOf("\"result\":\"") + 10;
        int endIndex = jsonResponse.lastIndexOf("\"");

        if (startIndex > 10 && endIndex > startIndex) {
            return jsonResponse.substring(startIndex, endIndex);
        }

        return jsonResponse; // Fallback: ritorna tutto
    }
}