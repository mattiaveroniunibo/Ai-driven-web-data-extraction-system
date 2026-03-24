package com.kayak.rest;

import com.kayak.entity.Event;
import com.kayak.service.EventService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@Path("/api/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventService eventService;

    @GET
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GET
    @Path("/pending")
    public List<Event> getPendingEvents() {
        return eventService.getPendingEvents();
    }

    @POST
    public Response createEvent(EventRequest request) {
        Event event = eventService.createEvent(
                request.url,
                request.tipologia,
                request.data
        );
        return Response.status(Response.Status.CREATED).entity(event).build();
    }

    @GET
    @Path("/{id}")
    public Response getEvent(@PathParam("id") Long id) {
        Event event = eventService.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(event).build();
    }

    public static class EventRequest {
        public String url;
        public String tipologia;        public LocalDateTime data;
    }
}