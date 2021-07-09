/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NpmHarvesterTest {
    private final NpmClient client = mock(NpmClient.class);
    private final NpmHarvester harvester = new NpmHarvester(client);

    @Test
    void triggersForSupportedType() {
        assertThat(harvester.isSupportedType("generic")).isFalse();
        assertThat(harvester.isSupportedType("npm")).isTrue();
    }
}
