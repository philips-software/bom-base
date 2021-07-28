/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.philips.research.bombase.core.license_cleaner.LicenseCleanerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.tlinkowski.annotation.basic.NullOr;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/licenses")
public class LicensesRoute {
    private final LicenseCleanerService service;

    LicensesRoute(LicenseCleanerService service) {
        this.service = service;
    }

    @PostMapping()
    void setLicenseCuration(@RequestBody CurationJson body) {
        if (body.license == null || body.curation == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Need license and curation in body");
        }
        service.defineCuration(body.license, body.curation);
    }

    static class CurationJson {
        @NullOr String license;
        @NullOr String curation;
    }
}
