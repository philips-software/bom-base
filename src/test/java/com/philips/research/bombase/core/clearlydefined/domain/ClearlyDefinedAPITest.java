/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedAPI.LicensedJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClearlyDefinedAPITest {
    @Test
    void convertsNoAssertDeclaredLicense() {
        final var licensed = new LicensedJson();
        licensed.declared = "NOASSERTION";

        assertThat(licensed.getDeclaredLicense()).isEmpty();
    }
}
