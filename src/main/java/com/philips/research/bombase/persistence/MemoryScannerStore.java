/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.persistence;

import com.philips.research.bombase.core.scanner.ScannerService;
import com.philips.research.bombase.core.scanner.ScannerStore;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryScannerStore implements ScannerStore {
    private final Map<URI, ScannerService.ScanResult> scans = new ConcurrentHashMap<>();

    @Override
    public void store(URI location, ScannerService.ScanResult scan) {
        scans.put(location, scan);
    }

    @Override
    public Optional<ScannerService.ScanResult> retrieve(URI location) {
        return Optional.ofNullable(scans.get(location));
    }
}
