/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner.domain;

import com.philips.research.bombase.core.license_cleaner.LicenseCleanerService;
import com.philips.research.bombase.core.license_cleaner.LicenseCleanerStore;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LicenseCleanerInteractorTest {
    private static final String URL = "http://example.com/license";
    private static final String LICENSE = "License";

    private final LicenseCleanerStore store = mock(LicenseCleanerStore.class);
    private final LicenseCleanerService interactor = new LicenseCleanerInteractor(store);

    @Test
    void storesCuration() {
        interactor.defineCuration(URL, LICENSE);

        verify(store).storeCuration(URL, LICENSE);
    }
}
