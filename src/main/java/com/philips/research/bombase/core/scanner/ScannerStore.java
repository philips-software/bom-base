/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner;

import java.net.URI;
import java.util.Optional;

/**
 * Persistence interface for scan results.
 */
public interface ScannerStore {
    /**
     * Persists the result of a scan
     *
     * @param uri  location of the scanned resources
     * @param scan scanning results
     */
    void store(URI uri, ScannerService.ScanResult scan);

    /**
     * Retrieves the prior scan result.
     *
     * @param uri location of the resources
     * @return prior scan results (if available)
     */
    Optional<ScannerService.ScanResult> retrieve(URI uri);
}
