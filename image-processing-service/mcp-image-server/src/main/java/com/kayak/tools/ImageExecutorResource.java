package com.kayak.tools;  // ← Cambiato da com.kayak.mcp

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path ;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@jakarta.ws.rs.Path("/mcp/execute")
public class ImageExecutorResource {

    private static final Logger LOG = Logger.getLogger(ImageExecutorResource.class);
    private static final String DOWNLOAD_DIR = "downloaded-images";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String execute(ExecuteRequest request) {
        LOG.info("Executing tool: " + request.tool + " with URL: " + request.url);

        if ("downloadImage".equals(request.tool)) {
            return downloadImage(request.url);
        }

        return "{\"error\":\"Unknown tool: " + request.tool + "\"}";
    }

    private String downloadImage(String imageUrl) {
        try {
            // Crea directory se non esiste
            Path dir = Paths.get(DOWNLOAD_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // Estrae nome file dall'URL
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            Path targetPath = dir.resolve(fileName);

            // Scarica l'immagine
            LOG.info("Downloading image from: " + imageUrl);
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            String result = "Image downloaded successfully to: " + targetPath.toAbsolutePath();
            LOG.info(result);
            return "{\"result\":\"" + result + "\"}";

        } catch (Exception e) {
            LOG.error("Error downloading image", e);
            return "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static class ExecuteRequest {
        public String tool;
        public String url;
    }
}