/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.license_cleaner.LicenseCleanerStore;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.scanner.ScannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Cleans up updated license values.
 */
@Service
public class LicenseCleaner implements MetaRegistry.PackageListener {
    private static final Logger LOG = LoggerFactory.getLogger(LicenseCleaner.class);
    private static final Pattern URL_PATTERN = Pattern.compile("https?:\\S+");

    private final LicenseCleanerStore store;
    private final ScannerService scanner;

    @Autowired
    public LicenseCleaner(LicenseCleanerStore store, ScannerService scanner) {
        this.store = store;
        this.scanner = scanner;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, Object> values) {
        final var current = (String) values.getOrDefault(Field.DECLARED_LICENSE, "");
        if (!updated.contains(Field.DECLARED_LICENSE) || !URL_PATTERN.matcher(current).find()) {
            return Optional.empty();
        }
        return Optional.of(this::clean);
    }

    void clean(PackageAttributeEditor editor) {
        editor.get(Field.DECLARED_LICENSE)
                .map(lic -> (String) lic)
                .map(lic -> URL_PATTERN.matcher(lic)
                        .replaceAll(result -> licenseFor(result.group())))
                .ifPresent(lic -> editor.update(Field.DECLARED_LICENSE, editor.trust(Field.DECLARED_LICENSE), lic));
    }

    private String licenseFor(String url) {
        return store.findCuration(url)
                .or(() -> scanLicense(url))
                .orElse(url);
    }

    private Optional<String> scanLicense(String url) {
        try {
            final var licenses = scanner.scanLicenses(URI.create(url));
            return !licenses.isEmpty() ? Optional.of(licenses.get(0)) : Optional.empty();
        } catch (Exception e) {
            LOG.warn("Scanning a license from {} failed", url, e);
            return Optional.empty();
        }
    }
}
