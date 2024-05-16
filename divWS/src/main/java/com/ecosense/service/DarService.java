package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public interface DarService {

    List<JsonNode> get(String deimsUUID);

}
