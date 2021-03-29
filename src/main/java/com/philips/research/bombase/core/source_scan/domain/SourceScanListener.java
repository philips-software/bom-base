/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.source_scan.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.downloader.DownloadService;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.scanner.ScannerService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SourceScanListener implements MetaRegistry.PackageListener {
    private final DownloadService downloader;
    private final ScannerService scanner;

    public SourceScanListener(DownloadService downloader, ScannerService scanner) {
        this.downloader = downloader;
        this.scanner = scanner;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, ?> values) {
        if (!updated.contains(Field.SOURCE_LOCATION)) {
            return Optional.empty();
        }

        return Optional.of(pkg -> harvest(purl, pkg));
    }

    private void harvest(PackageURL purl, PackageAttributeEditor pkg) {
        pkg.<URI>get(Field.SOURCE_LOCATION)
                .ifPresent(location -> downloader.download(location, directory -> {
                    final var expression = licensesScannedIn(directory).stream()
                            .map(ScannerService.LicenseResult::getExpression)
                            .collect(Collectors.joining("\n"));
                    pkg.update(Field.DETECTED_LICENSE, 100, expression);
                }));
    }

    private List<ScannerService.LicenseResult> licensesScannedIn(Path directory) {
        return scanner.scan(directory).getLicenses();
    }
}
