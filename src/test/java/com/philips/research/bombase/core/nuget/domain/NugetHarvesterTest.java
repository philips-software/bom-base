/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.nuget.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NugetHarvesterTest {
    private final NugetClient client = mock(NugetClient.class);
    private final NugetHarvester harvester = new NugetHarvester(client);

    @Test
    void triggersForSupportedType() {
        assertThat(harvester.isSupportedType("generic")).isFalse();
        assertThat(harvester.isSupportedType("nuget")).isTrue();
    }
}
