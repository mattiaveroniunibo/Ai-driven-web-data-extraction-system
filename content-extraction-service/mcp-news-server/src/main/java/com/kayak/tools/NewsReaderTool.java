package com.kayak.tools;

import io.quarkiverse.mcp.server.Tool;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@ApplicationScoped
public class NewsReaderTool {

    @Tool(description = "Reads content from a URL and returns a summary")
    public String readNews(String url) {
        try {
            // Connessione HTTP
            URL urlObj = URI.create(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Leggi il contenuto
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String fullContent = content.toString();

            // Crea un riepilogo semplice
            String summary = createSimpleSummary(fullContent);

            // Ritorna JSON-like string
            return String.format(
                    "{\"url\": \"%s\", \"contentLength\": %d, \"summary\": \"%s\", \"preview\": \"%s\"}",
                    url,
                    fullContent.length(),
                    summary,
                    getPreview(fullContent)
            );

        } catch (Exception e) {
            return String.format(
                    "{\"error\": \"Failed to read URL: %s\", \"message\": \"%s\"}",
                    url,
                    e.getMessage()
            );
        }
    }

    private String createSimpleSummary(String content) {
        // Rimuovi tag HTML
        String cleanContent = content.replaceAll("<[^>]*>", " ");
        cleanContent = cleanContent.replaceAll("\\s+", " ").trim();

        // Conta parole
        int wordCount = cleanContent.split("\\s+").length;

        return String.format("Content retrieved successfully with approximately %d words", wordCount);
    }

    private String getPreview(String content) {
        // Rimuovi tag HTML
        String cleanContent = content.replaceAll("<[^>]*>", " ");
        cleanContent = cleanContent.replaceAll("\\s+", " ").trim();

        // Prendi primi 200 caratteri
        if (cleanContent.length() > 200) {
            return cleanContent.substring(0, 200).replace("\"", "'") + "...";
        }
        return cleanContent.replace("\"", "'");
    }
}