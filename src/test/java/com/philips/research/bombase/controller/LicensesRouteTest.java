/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.philips.research.bombase.core.license_cleaner.LicenseCleanerService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LicensesRoute.class, JacksonConfiguration.class})
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class LicensesRouteTest {
    private static final String CURATION_URL = "/licenses";
    private static final String LICENSE_URL = "https://example.com/license";
    private static final String LICENSE = "License";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LicenseCleanerService service;

    @BeforeEach
    void beforeEach() {
        Mockito.reset(service);
    }

    @Test
    void registersLicenseCuration() throws Exception {
        final var json = new JSONObject()
                .put("license", LICENSE_URL)
                .put("curation", LICENSE);
        mvc.perform(post(CURATION_URL)
                .content(json.toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).defineCuration(LICENSE_URL, LICENSE);
    }

    @Test
    void badRequest_registerLicenseWithoutParameters() throws Exception {
        mvc.perform(post(CURATION_URL)
                .content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
