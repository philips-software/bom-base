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
    private static final String PURL = "pkg:maven/name@version";
    private static final String KEY = "Key";
    private static final String VALUE = "Value";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MetaService service;

    private static String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(service);
    }

    @Test
    void providesPackageDetails() throws Exception {
        when(service.getAttributes(new PackageURL(PURL))).thenReturn(Map.of(KEY, VALUE));

        mvc.perform(get(URL_PACKAGE, encode(PURL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purl").value(PURL))
                .andExpect(jsonPath("$.attributes." + KEY).value(VALUE));
    }
}
