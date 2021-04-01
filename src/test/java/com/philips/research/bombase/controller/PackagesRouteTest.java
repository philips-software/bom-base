/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PackagesRoute.class, JacksonConfiguration.class})
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class PackagesRouteTest {
    private static final String URL_PACKAGES = "/packages";
    private static final String URL_PACKAGE = URL_PACKAGES + "/{purl}";
    private static final String TYPE = "type";
    private static final String NAMESPACE = "n%40mespace";
    private static final String NAME = "n%40me";
    private static final String VERSION = "vers%2Fon";
    private static final String PURL = String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION);
    private static final String KEY = "Key";
    private static final String VALUE = "Value";
    private static final Instant TIMESTAMP = Instant.now();

    private final MetaService.PackageDto packageDto = new MetaService.PackageDto();

    @Autowired
    private MockMvc mvc;
    @MockBean
    private MetaService service;

    private static String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void beforeEach() throws Exception {
        Mockito.reset(service);
        packageDto.updated = TIMESTAMP;
        packageDto.purl = new PackageURL(PURL);
    }

    @Test
    void providesPackageDetails() throws Exception {
        when(service.getAttributes(new PackageURL(PURL))).thenReturn(Map.of(KEY, VALUE));

        mvc.perform(get(URL_PACKAGE, encode(PURL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purl").value(PURL))
                .andExpect(jsonPath("$.attributes." + KEY).value(VALUE));
    }

    @Test
    void notFound_noMetadataForPackage() throws Exception {
        when(service.getAttributes(new PackageURL(PURL))).thenReturn(Map.of());

        mvc.perform(get(URL_PACKAGE, encode(PURL)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listsLatestScans() throws Exception {
        when(service.latestScans()).thenReturn(List.of(packageDto));

        mvc.perform(get(URL_PACKAGES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(encode(encode(PURL))))
                .andExpect(jsonPath("$.results[0].purl").value(PURL))
                .andExpect(jsonPath("$.results[0].attributes").doesNotExist());
    }

    @Test
    void searchesPackages() throws Exception {
        when(service.search(TYPE, NAMESPACE, NAME, VERSION)).thenReturn(List.of(packageDto));

        mvc.perform(get(URL_PACKAGES)
                .queryParam("type", TYPE)
                .queryParam("ns", NAMESPACE)
                .queryParam("name", NAME)
                .queryParam("version", VERSION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].purl").value(PURL));
    }

    @Test
    void defaultsMissingSearchParametersToEmptyString() throws Exception {
        when(service.search("", "", "", "")).thenReturn(List.of(packageDto));

        mvc.perform(get(URL_PACKAGES)
                .queryParam("type", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].purl").value(PURL));
    }
}
