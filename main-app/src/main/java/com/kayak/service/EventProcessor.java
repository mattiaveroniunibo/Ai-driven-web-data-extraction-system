package com.kayak.service;

import com.kayak.entity.ImageResult;
import com.kayak.entity.NewsResult;
import jakarta.transaction.Transactional;
import com.kayak.entity.Event;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class EventProcessor {

    private static final Logger LOG = Logger.getLogger(EventProcessor.class);

    @Inject
    EventService eventService;

    @Inject
    McpClientService mcpClientService;

    @Inject
    AiOrchestratorService aiOrchestrator;

    @Scheduled(every = "30s")
    public void processEvents() {
        LOG.info("Checking for pending events...");

        List<Event> pendingEvents = eventService.getPendingEvents();

        if (pendingEvents.isEmpty()) {
            LOG.debug("No pending events to process");
            return;
        }

        LOG.info("Processing " + pendingEvents.size() + " events");

        for (Event event : pendingEvents) {
            try {
                String risultato = processEvent(event);
                eventService.markAsProcessed(event, risultato);
                LOG.info("Event " + event.id + " processed successfully");
            } catch (Exception e) {
                LOG.error("Error processing event " + event.id, e);
                eventService.markAsProcessed(event, "ERROR: " + e.getMessage());
            }
        }
    }

    private String processEvent(Event event) {
        LOG.info("Processing event: " + event.tipologia + " - " + event.url);

        // Usa AI per decidere quale tool usare
        String toolDecision = aiOrchestrator.decideTool(event.url);
        LOG.info(" AI decided to use tool: " + toolDecision);

        String result;

        // Chiama il tool appropriato basandosi sulla decisione dell'AI
        if (toolDecision.toLowerCase().contains("image")) {
            LOG.info("Calling IMAGE tool based on AI decision");
            result = mcpClientService.downloadImage(event.url);

            // Salva in tabella ImageResult
            saveImageResult(event, result);

        } else {
            LOG.info("Calling NEWS tool based on AI decision");
            result = mcpClientService.readNews(event.url);

            // Salva in tabella NewsResult
            saveNewsResult(event, result);
        }

        return result;
    }

    @Transactional
    public void saveImageResult(Event event, String result) {
        try {
            ImageResult imageResult = new ImageResult(event, result);
            imageResult.persist();
            LOG.info("Saved image result to ImageResult table for event " + event.id);
        } catch (Exception e) {
            LOG.error("Error saving image result", e);
        }
    }

    @Transactional
    public void saveNewsResult(Event event, String result) {
        try {
            // Prendi primi 500 caratteri come summary
            String summary = result.length() > 500 ? result.substring(0, 500) : result;
            NewsResult newsResult = new NewsResult(event, result, summary);
            newsResult.persist();
            LOG.info("Saved news result to NewsResult table for event " + event.id);
        } catch (Exception e) {
            LOG.error("Error saving news result", e);
        }
    }
}