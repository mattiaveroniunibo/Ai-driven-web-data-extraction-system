package com.kayak.service;

import com.kayak.entity.Tipologia;
import com.kayak.entity.TipologiaField;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class TipologiaService {

    private static final Logger LOG = Logger.getLogger(TipologiaService.class);

    @Transactional
    public Tipologia createTipologia(String name, String description, String mcpToolName, List<TipologiaField> fields) {
        // Verifica che non esista già
        Tipologia existing = Tipologia.findByName(name);
        if (existing != null) {
            throw new IllegalArgumentException("Tipologia with name '" + name + "' already exists");
        }

        Tipologia tipologia = new Tipologia();
        tipologia.name = name;
        tipologia.description = description;
        tipologia.mcpToolName = (mcpToolName != null) ? mcpToolName : "process" + name;        tipologia.persist();

        // Aggiungi campi
        if (fields != null) {
            for (TipologiaField field : fields) {
                field.tipologia = tipologia;
                field.persist();
            }
        }

        LOG.info("Created tipologia: " + name + " with " + (fields != null ? fields.size() : 0) + " fields");
        return tipologia;
    }

    public List<Tipologia> getAllTipologie() {
        return Tipologia.listAll();
    }

    public Tipologia getTipologiaById(Long id) {
        return Tipologia.findById(id);
    }

    public Tipologia getTipologiaByName(String name) {
        return Tipologia.findByName(name);
    }

    @Transactional
    public void deleteTipologia(Long id) {
        Tipologia tipologia = Tipologia.findById(id);
        if (tipologia != null) {
            tipologia.delete();
            LOG.info("Deleted tipologia: " + tipologia.name);
        }
    }
}