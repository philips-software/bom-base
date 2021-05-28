/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

class PackageJsonTest {
    private static final ObjectMapper MAPPER = new JacksonConfiguration().objectMapper();
    private static final String PURL = "pkg:type/namespace/name@version";
    private static final String ID = "pkg%253Atype%252Fnamespace%252Fname%2540version";
    private static final String TIMESTAMP = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC))
            .format(Instant.ofEpochSecond(12345678));

    @Test
    void createsFromPurl() throws Exception {
        final var expected = new JSONObject()
                .put("id", ID)
                .put("purl", PURL);
        final var purl = new PackageURL(PURL);

        final var json = new PackageJson(purl);

        JSONAssert.assertEquals(expected.toString(), MAPPER.writeValueAsString(json), false);
    }

    @Test
    void createsFromDto() throws Exception {
        final var expected = new JSONObject()
                .put("id", ID)
                .put("purl", PURL)
                .put("updated", TIMESTAMP);
        final var dto = new MetaService.PackageDto();
        dto.purl = new PackageURL(PURL);
        dto.updated = Instant.parse(TIMESTAMP);

        final var json = new PackageJson(dto);

        JSONAssert.assertEquals(expected.toString(), MAPPER.writeValueAsString(json), true);
    }
}
