package com.kayak.service;

import com.kayak.entity.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class EventService {

    @Transactional
    public Event createEvent(String url, String tipologia, LocalDateTime data) {        Event event = new Event();
        event.url = url;
        event.tipologia = tipologia;
        event.data = data;
        event.lavorato = false;
        event.persist();
        return event;
    }


    public List<Event> getAllEvents() {
        return Event.listAll();
    }

    public List<Event> getPendingEvents() {
        return Event.list("lavorato = false AND data <= ?1", LocalDateTime.now());
    }

    @Transactional
    public void markAsProcessed(Event event, String risultato) {
        Event managedEvent = Event.findById(event.id);
        if (managedEvent!= null) {
            managedEvent.lavorato = true;
            managedEvent.risultato = risultato;
        }
    }

    public Event findById(Long id) {
        return Event.findById(id);
    }
}