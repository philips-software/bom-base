/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PyPiHarvesterTest {
    private final PyPiClient client = mock(PyPiClient.class);
    private final PyPiHarvester harvester = new PyPiHarvester(client);

    @Test
    void triggersForSupportedType() {
        assertThat(harvester.isSupportedType("generic")).isFalse();
        assertThat(harvester.isSupportedType("pypi")).isTrue();
    }
}
