/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner.domain;

import com.philips.research.bombase.core.license_cleaner.LicenseCleanerService;
import com.philips.research.bombase.core.license_cleaner.LicenseCleanerStore;
import org.springframework.stereotype.Service;

@Service
public class LicenseCleanerInteractor implements LicenseCleanerService {
    private final LicenseCleanerStore store;

    public LicenseCleanerInteractor(LicenseCleanerStore store) {
        this.store = store;
    }

    @Override
    public void defineCuration(String url, String license) {
        store.storeCuration(url, license);
    }
}
