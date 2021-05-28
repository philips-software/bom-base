/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.research.bombase.core.MetaService.AttributeDto;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Map;

class AttributesJsonTest {
    private static final ObjectMapper MAPPER = new JacksonConfiguration().objectMapper();
    private static final String KEY = "Key";
    private static final int SCORE = 42;
    private static final int ALT_SCORE = 23;
    private static final String VALUE = "Value";
    private static final String ALT_VALUE = "Alt value";

    @Test
    void createsInstance() throws Exception {
        final var expected = new JSONObject()
                .put("attributes", new JSONObject()
                        .put(KEY, new JSONObject()
                                .put("score", SCORE)
                                .put("value", VALUE)
                                .put("alt_score", ALT_SCORE)
                                .put("alt_value", ALT_VALUE)));
        final var dto = new AttributeDto();
        dto.score = SCORE;
        dto.value = VALUE;
        dto.altScore = ALT_SCORE;
        dto.altValue = ALT_VALUE;

        final var json = new AttributesJson(Map.of(KEY, dto));

        JSONAssert.assertEquals(expected.toString(), MAPPER.writeValueAsString(json), true);
    }

    @Test
    void ignoresMissingAltValue() throws Exception {
        final var expected = new JSONObject()
                .put("attributes", new JSONObject()
                        .put(KEY, new JSONObject()
                                .put("score", 0)));
        final var dto = new AttributeDto();

        final var json = new AttributesJson(Map.of(KEY, dto));

        JSONAssert.assertEquals(expected.toString(), MAPPER.writeValueAsString(json), true);
    }
}
