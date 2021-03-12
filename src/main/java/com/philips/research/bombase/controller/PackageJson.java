/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.github.packageurl.PackageURL;

import java.util.Map;

class PackageJson {
    String purl;
    Map<String, Object> attributes;

    PackageJson(PackageURL purl, Map<String, Object> attributes) {
        this.purl = purl.canonicalize();
        this.attributes = attributes;
    }
}
