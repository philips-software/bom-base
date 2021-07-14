/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MavenHarvesterTest {
    private final MavenClient client = mock(MavenClient.class);
    private final MavenHarvester harvester = new MavenHarvester(client);

    @Test
    void triggersForSupportedType() {
        assertThat(harvester.isSupportedType("generic")).isFalse();
        assertThat(harvester.isSupportedType("maven")).isTrue();
    }
}
