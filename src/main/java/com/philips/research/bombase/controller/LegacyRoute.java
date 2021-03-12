/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Map;

@RestController
@RequestMapping()
public class LegacyRoute {
    private final MetaService service;

    public LegacyRoute(MetaService service) {
        this.service = service;
    }

    @PostMapping("/packages")
    LicenseJson getLicense(@RequestBody RequestJson body) {
        //TODO Only query, and leave harvesting to core layer
        try {
            if (body.purl != null) {
                service.setAttributes(new PackageURL(body.purl), Map.of());
            }
        } catch (MalformedPackageURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid Package URL");
        }
        return new LicenseJson();
    }

    static class RequestJson {
        @NullOr String purl;
    }

    static class LicenseJson {
        String id;
        String license;
    }
}
