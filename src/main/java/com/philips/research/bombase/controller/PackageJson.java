/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.github.packageurl.PackageURL;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PackageJson {
    final String id;
    final String purl;
    final @NullOr Map<String, Object> attributes;

    PackageJson(PackageURL purl) {
        this(purl, null);
    }

    PackageJson(PackageURL purl, @NullOr  Map<String, Object> attributes) {
        this.purl = purl.canonicalize();
        this.id = encode(this.purl);
        this.attributes = attributes;
    }

    private static String encode(String purl) {
        return URLEncoder.encode(purl, StandardCharsets.UTF_8);
    }

    public static List<PackageJson> fromList(List<PackageURL> purls) {
        return purls.stream().map(PackageJson::new).collect(Collectors.toList());
    }
}
