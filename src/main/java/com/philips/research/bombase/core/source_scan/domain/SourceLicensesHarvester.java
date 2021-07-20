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
import com.philips.research.bombase.core.meta.registry.Trust;
import com.philips.research.bombase.core.scanner.ScannerService;
import com.philips.research.bombase.core.source_scan.SourceScanException;
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
public class SourceLicensesHarvester implements MetaRegistry.PackageListener {
    //TODO Is this even a realistic maximum score?
    static final int MAX_SCORE = 80;

    private final DownloadService downloader;
    private final ScannerService scanner;

    public SourceLicensesHarvester(DownloadService downloader, ScannerService scanner) {
        this.downloader = downloader;
        this.scanner = scanner;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, Object> values) {
        if (!updated.contains(Field.SOURCE_LOCATION)) {
            return Optional.empty();
        }

        return Optional.of(pkg -> harvest(purl, pkg));
    }

    private void harvest(PackageURL purl, PackageAttributeEditor pkg) {
        try {
            pkg.<String>get(Field.SOURCE_LOCATION)
                    .map(location -> downloader.download(URI.create(location), this::licensesScannedIn))
                    .ifPresent(detections -> {
                        final var expressions = licensesIn(detections);
                        pkg.update(Field.DETECTED_LICENSES, Trust.PROBABLY, expressions);
                    });
        } catch (Exception e) {
            throw new SourceScanException("Failed to scan licenses for " + purl, e);
        }
    }

    private List<String> licensesIn(List<ScannerService.LicenseResult> detections) {
        return detections.stream()
                .map(ScannerService.LicenseResult::getExpression)
                .collect(Collectors.toList());
    }

    private List<ScannerService.LicenseResult> licensesScannedIn(Path directory) {
        return scanner.scan(directory).getLicenses();
    }
}
