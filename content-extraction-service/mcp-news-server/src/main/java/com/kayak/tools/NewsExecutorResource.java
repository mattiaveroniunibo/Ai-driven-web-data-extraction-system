package com.kayak.tools;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@jakarta.ws.rs.Path("/mcp/execute")
public class NewsExecutorResource {

    private static final Logger LOG = Logger.getLogger(NewsExecutorResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String execute(ExecuteRequest request) {
        LOG.info("Executing tool: " + request.tool + " with URL: " + request.url);

        if ("readNews".equals(request.tool)) {
            return readNews(request.url);
        }

        return "{\"error\":\"Unknown tool: " + request.tool + "\"}";
    }

    private String readNews(String newsUrl) {
        try {
            LOG.info("Reading news from: " + newsUrl);

            // Scarica contenuto HTML
            URL url = new URL(newsUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String htmlContent = in.lines().collect(Collectors.joining("\n"));
            in.close();

            // Rimuovi tag HTML per estrarre testo
            String cleanText = htmlContent
                    .replaceAll("<script[^>]*>.*?</script>", "")  // Rimuovi script
                    .replaceAll("<style[^>]*>.*?</style>", "")    // Rimuovi style
                    .replaceAll("<[^>]+>", " ")                   // Rimuovi tutti i tag
                    .replaceAll("\\s+", " ")                      // Normalizza spazi
                    .trim();

            // Prendi i primi 500 caratteri come sommario
            String summary = cleanText.length() > 500
                    ? cleanText.substring(0, 500) + "..."
                    : cleanText;

            LOG.info("News read successfully. Content length: " + cleanText.length() + " chars");

            // Escape caratteri JSON
            summary = summary.replace("\"", "'").replace("\n", " ").replace("\r", "");

            return "{\"result\":\"" + summary + "\"}";

        } catch (Exception e) {
            LOG.error("Error reading news", e);
            String errorMsg = e.getMessage().replace("\"", "'");
            return "{\"error\":\"" + errorMsg + "\"}";
        }
    }

    public static class ExecuteRequest {
        public String tool;
        public String url;
    }
}