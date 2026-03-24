package com.kayak.rest;

import com.kayak.entity.Tipologia;
import com.kayak.entity.TipologiaField;
import com.kayak.service.TipologiaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/tipologie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipologiaResource {

    @Inject
    TipologiaService tipologiaService;

    @GET
    public List<Tipologia> listAll() {
        return tipologiaService.getAllTipologie();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Tipologia tipologia = tipologiaService.getTipologiaById(id);
        if (tipologia == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tipologia).build();
    }

    @POST
    public Response create(CreateTipologiaRequest request) {
        try {
            Tipologia tipologia = tipologiaService.createTipologia(
                    request.name,
                    request.description,
                    request.mcpToolName,
                    request.fields
            );
            return Response.status(Response.Status.CREATED).entity(tipologia).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        tipologiaService.deleteTipologia(id);
        return Response.noContent().build();
    }

    // DTO per la richiesta
    public static class CreateTipologiaRequest {
        public String name;
        public String description;
        public String mcpToolName;
        public List<TipologiaField> fields;
    }
}