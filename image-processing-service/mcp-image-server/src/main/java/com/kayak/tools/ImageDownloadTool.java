package com.kayak.tools;

import io.quarkiverse.mcp.server.Tool;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class ImageDownloadTool {

    private static final String DOWNLOAD_DIR = "downloads/images";

    @Tool(description = "Downloads an image from the given URL and saves it locally")    public String downloadImage(String imageUrl) {
        try {
            // Crea la directory se non esiste
            Path downloadPath = Paths.get(DOWNLOAD_DIR);
            Files.createDirectories(downloadPath);

            // Estrai il nome del file dall'URL
            String fileName = extractFileName(imageUrl);
            Path filePath = downloadPath.resolve(fileName);

            // Scarica l'immagine
            URL url = URI.create(imageUrl).toURL();
            try (InputStream in = url.openStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return "Image downloaded successfully: " + filePath.toAbsolutePath();
        } catch (IOException e) {
            return "Failed to download image: " + e.getMessage();
        }
    }

    private String extractFileName(String url) {
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];

        // Se non ha estensione, aggiungi .jpg di default
        if (!fileName.contains(".")) {
            fileName += ".jpg";
        }

        return fileName;
    }
}