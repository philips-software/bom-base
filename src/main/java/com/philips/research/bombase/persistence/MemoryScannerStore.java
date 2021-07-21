/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.persistence;

import com.philips.research.bombase.core.scanner.ScannerService;
import com.philips.research.bombase.core.scanner.ScannerStore;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryScannerStore implements ScannerStore {
    private final Map<URI, ScannerService.ScanResult> scans = new HashMap<>();

    @Override
    public void store(URI uri, ScannerService.ScanResult scan) {
        scans.put(uri, scan);
    }

    @Override
    public Optional<ScannerService.ScanResult> retrieve(URI uri) {
        return Optional.ofNullable(scans.get(uri));
    }
}
