/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedAPI.DescribedJson;
import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedAPI.LicensedJson;
import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedAPI.ResponseJson;
import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedAPI.ScoreJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClearlyDefinedAPITest {
    private static final int MAX_SCORE = 70;

    @Test
    void derivesMetadataScore() {
        final var response = new ResponseJson();
        response.described = new DescribedJson();
        response.described.score = new ScoreJson();
        response.described.score.total = 50;

        assertThat(response.getDescribedScore()).isEqualTo((int) (50 / 100f * MAX_SCORE));
    }

    @Test
    void derivesLicenseScore() {
        final var response = new ResponseJson();
        response.licensed = new LicensedJson();
        response.licensed.score = new ScoreJson();
        response.licensed.score.total = 50;

        assertThat(response.getLicensedScore()).isEqualTo((int) (50 / 100f * MAX_SCORE));
    }

    @Test
    void convertsNoAssertDeclaredLicense() {
        final var licensed = new LicensedJson();
        licensed.declared = "NOASSERTION";

        assertThat(licensed.getDeclaredLicense()).isEmpty();
    }
}
