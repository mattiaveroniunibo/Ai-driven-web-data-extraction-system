package com.kayak;

import com.kayak.entity.Tipologia;
import com.kayak.entity.TipologiaField;
import com.kayak.service.TipologiaService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class StartupInitializer {

    private static final Logger LOG = Logger.getLogger(StartupInitializer.class);

    @Inject
    TipologiaService tipologiaService;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Initializing default tipologie...");

        // Crea tipologia IMAGE se non esiste
        if (Tipologia.findByName("IMAGE") == null) {
            List<TipologiaField> imageFields = new ArrayList<>();
            imageFields.add(new TipologiaField("url", "URL", true));

            tipologiaService.createTipologia(
                    "IMAGE",
                    "Download images from URL",
                    "downloadImage",
                    imageFields
            );
            LOG.info("Created IMAGE tipologia");
        }

        // Crea tipologia NEWS se non esiste
        if (Tipologia.findByName("NEWS") == null) {
            List<TipologiaField> newsFields = new ArrayList<>();
            newsFields.add(new TipologiaField("url", "URL", true));

            tipologiaService.createTipologia(
                    "NEWS",
                    "Read and summarize news from web pages",
                    "readNews",
                    newsFields
            );
            LOG.info("Created NEWS tipologia");
        }

        LOG.info("Tipologie initialization complete!");
    }
}