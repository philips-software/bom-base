/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ClearlyDefinedHarvesterTest {
    private final ClearlyDefinedClient client = mock(ClearlyDefinedClient.class);
    private final ClearlyDefinedHarvester harvester = new ClearlyDefinedHarvester(client);

    @Test
    void triggersForSupportedTypes() {
        assertThat(harvester.isSupportedType("generic")).isFalse();
        assertThat(harvester.isSupportedType("npm")).isTrue();
        assertThat(harvester.isSupportedType("gem")).isTrue();
        assertThat(harvester.isSupportedType("pypi")).isTrue();
        assertThat(harvester.isSupportedType("maven")).isTrue();
        assertThat(harvester.isSupportedType("nuget")).isTrue();
        assertThat(harvester.isSupportedType("github")).isTrue();
        assertThat(harvester.isSupportedType("cargo")).isTrue();
        assertThat(harvester.isSupportedType("deb")).isTrue();
        assertThat(harvester.isSupportedType("composer")).isTrue();
        assertThat(harvester.isSupportedType("cocoapods")).isTrue();
    }
}
