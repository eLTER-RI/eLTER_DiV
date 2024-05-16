package com.ecosense.controller;

import java.util.List;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.service.DarService;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/dar")
public class DarController {

    @Autowired
    private DarService darService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Response get(@QueryParam("deimsUUID") String deimsUUID) {
        SimpleResponseDTO response = new SimpleResponseDTO();
        List<JsonNode> res = null;
        try {
            res = darService.get(deimsUUID);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
        }
        if (response.getStatus() == SimpleResponseDTO.OK) {
            return Response.ok(res).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

}
