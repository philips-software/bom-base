/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.license_cleaner.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import com.philips.research.bombase.core.scanner.ScannerService;
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
    private static final Pattern URL_PATTERN = Pattern.compile("https?:\\S+");

    private final ScannerService scanner;

    @Autowired
    public LicenseCleaner(ScannerService scanner) {
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
        try {
            final var licenses = scanner.scanLicenses(URI.create(url));
            return !licenses.isEmpty() ? licenses.get(0) : url;
        } catch (Exception e) {
            return url;
        }
    }
}
