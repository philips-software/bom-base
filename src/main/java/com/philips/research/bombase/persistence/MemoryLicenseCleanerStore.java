/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.persistence;

import com.philips.research.bombase.core.license_cleaner.LicenseCleanerStore;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryLicenseCleanerStore implements LicenseCleanerStore {
    private final Map<String, String> curations = new ConcurrentHashMap<>();

    @Override
    public Optional<String> findCuration(String license) {
        return Optional.ofNullable(curations.get(license.toLowerCase()));
    }

    @Override
    public void storeCuration(String license, String curation) {
        curations.put(license.toLowerCase(), curation);
    }
}
