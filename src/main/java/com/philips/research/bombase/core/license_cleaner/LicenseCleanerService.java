/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner;

/**
 * Configuration of the license cleaner.
 */
public interface LicenseCleanerService {
    /**
     * Configures a curation for a URL as override to scanning the content of the URL.
     */
    void defineCuration(String url, String license);
}
