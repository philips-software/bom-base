/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService.PackageDto;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PackageJson {
    final String id;
    final String purl;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    final @NullOr Instant updated;
    final @NullOr Map<String, Object> attributes;

    PackageJson(PackageURL purl) {
        this(purl, null);
    }

    PackageJson(PackageURL purl, @NullOr Map<String, Object> attributes) {
        this.purl = purl.canonicalize();
        this.id = encode(this.purl);
        this.attributes = attributes;
        this.updated = null;
    }

    PackageJson(PackageDto dto) {
        this.purl = dto.purl.canonicalize();
        this.id = encode(this.purl);
        this.updated = dto.updated;
        attributes = null;
    }

    private static String encode(String purl) {
        return URLEncoder.encode(purl, StandardCharsets.UTF_8);
    }

    public static List<PackageJson> fromPurlList(List<PackageURL> purls) {
        return purls.stream().map(PackageJson::new).collect(Collectors.toList());
    }

    public static List<PackageJson> fromDtoList(List<PackageDto> dtoList) {
        return dtoList.stream().map(PackageJson::new).collect(Collectors.toList());
    }
}
