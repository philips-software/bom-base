/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import com.philips.research.bombase.core.downloader.DownloadService;
import com.philips.research.bombase.core.scanner.ScannerService;
import com.philips.research.bombase.core.scanner.ScannerStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScannerInteractor implements ScannerService {
    private static final Logger LOG = LoggerFactory.getLogger(ScannerInteractor.class);

    private final ScannerStore store;
    private final DownloadService downloader;
    private final ScanCodeScanner scanner;

    @Autowired
    public ScannerInteractor(ScannerStore store, DownloadService downloader) {
        this(store, downloader, new ScanCodeScanner());
    }

    ScannerInteractor(ScannerStore store, DownloadService downloader, ScanCodeScanner scanner) {
        this.store = store;
        this.downloader = downloader;
        this.scanner = scanner;
    }

    @Override
    public List<String> scanLicenses(URI location) {
        final var licenses = loadOrScan(location)
                .getLicenses().stream()
                .sorted((l, r) -> Integer.compare(r.getScore(), l.getScore()))
                .map(LicenseResult::getExpression)
                .collect(Collectors.toList());
        LOG.info("Scanned {} =>{}", location, licenses);
        return licenses;
    }

    private ScanResult loadOrScan(URI location) {
        return store.retrieve(location)
                .orElseGet(() -> {
                    final var result = downloader.download(location, scanner::scan);
                    store.store(location, result);
                    return result;
                });
    }
}
