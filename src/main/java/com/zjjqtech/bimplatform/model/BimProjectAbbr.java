package com.zjjqtech.bimplatform.model;

import com.fasterxml.jackson.databind.JsonNode;

public interface BimProjectAbbr {

    String getId();

    String getName();

    JsonNode getExt();
}
