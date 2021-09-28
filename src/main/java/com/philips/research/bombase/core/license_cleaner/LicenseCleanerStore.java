/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner;

import java.util.Optional;

/**
 * Persistence interface for the LicenseCleaner.
 */
public interface LicenseCleanerStore {
    /**
     * @return (optional) curation for the license (URL)
     */
    Optional<String> findCuration(String license);

    /**
     * Persists a license curation
     */
    void storeCuration(String license, String curation);
}
