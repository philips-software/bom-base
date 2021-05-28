/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.UnknownPackageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping()
public class LegacyRoute {
    private final MetaService service;

    public LegacyRoute(MetaService service) {
        this.service = service;
    }

    @PostMapping("/packages")
    LicenseJson getLicense(@RequestBody RequestJson body) {
        try {
            final var purl = new PackageURL(body.purl);
            final @NullOr String license = getDetectedLicenseOrCreatePackage(purl);
            return new LicenseJson(purl, license);
        } catch (MalformedPackageURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid Package URL: " + body.purl);
        }
    }

    private @NullOr String getDetectedLicenseOrCreatePackage(PackageURL purl) {
        try {
            final @NullOr Object value = service.getAttributes(purl).get("detected_license").value;
            return toMultilineString(value);
        } catch (UnknownPackageException e) {
            service.createPackage(purl);
            return null;
        }
    }

    private @NullOr String toMultilineString(@NullOr Object value) {
        //noinspection unchecked
        return (value != null)
                ? String.join("\n", (List<String>) value)
                : null;
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    static class RequestJson {
        String purl;
    }

    static class LicenseJson {
        String id;
        @NullOr String license;

        public LicenseJson(PackageURL purl, @NullOr String license) {
            this.id = encode(purl.canonicalize());
            this.license = license;
        }

        private static String encode(String purl) {
            return URLEncoder.encode(purl, StandardCharsets.UTF_8);
        }
    }
}
