/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/packages")
public class PackagesRoute {
    private final MetaService service;

    PackagesRoute(MetaService service) {
        this.service = service;
    }

    @GetMapping("{purl}")
    PackageJson getPackage(@PathVariable String purl) {
        final var pkgUrl = packageUrl(purl);
        final var attributes = service.getAttributes(pkgUrl);
        return new PackageJson(pkgUrl, attributes);
    }

    private PackageURL packageUrl(String purl) {
        try {
            return new PackageURL(URLDecoder.decode(purl, StandardCharsets.UTF_8));
        } catch (MalformedPackageURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Package URL parameter: " + purl);
        }
    }
}
