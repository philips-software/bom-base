/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.maven;

import java.net.URI;

public interface MavenService {
    void init();

    void updatePackage(URI purl, String group, String name, String version);
}
