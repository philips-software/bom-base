/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class ClearlyDefinedListener implements MetaRegistry.PackageListener {
    private static final Map<String, String> PROVIDERS = Map.of("maven", "mavencentral", "npm", "npmjs");

    private final ClearlyDefinedClient client;

    @Autowired
    public ClearlyDefinedListener() {
        this(new ClearlyDefinedClient());
    }

    ClearlyDefinedListener(ClearlyDefinedClient client) {
        this.client = client;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> fields, Map<Field, ?> values) {
        if (!fields.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pkg -> harvest(purl, pkg));
    }

    private void harvest(PackageURL purl, PackageAttributeEditor pkg) {
        readPackage(purl).ifPresent(def -> {
            int metaScore = def.getDescribedScore();
            int licenseScore = def.getLicensedScore();
            storeField(pkg, Field.SOURCE_LOCATION, metaScore, def.getSourceLocation());
            storeField(pkg, Field.DOWNLOAD_LOCATION, metaScore, def.getDownloadLocation());
            storeField(pkg, Field.HOME_PAGE, metaScore, def.getHomepage());
            storeField(pkg, Field.ATTRIBUTION, metaScore, def.getAuthors());
            storeField(pkg, Field.DECLARED_LICENSE, metaScore, def.getDeclaredLicense());
            storeField(pkg, Field.DETECTED_LICENSE, licenseScore, joinByAnd(def.getDetectedLicenses()));
            storeField(pkg, Field.SHA1, metaScore, def.getSha1());
            storeField(pkg, Field.SHA256, metaScore, def.getSha256());
        });
    }

    private <T> void storeField(PackageAttributeEditor pkg, Field field, int score, Optional<T> value) {
        value.ifPresent(v -> pkg.update(field, score, v));
    }

    private Optional<PackageDefinition> readPackage(PackageURL purl) {
        //TODO What about multiple provides for a single type?
        final var provider = PROVIDERS.getOrDefault(purl.getType(), purl.getType());

        return client.getPackageDefinition(purl.getType(), provider, purl.getNamespace(), purl.getName(), purl.getVersion());
    }

    Optional<String> joinByAnd(List<String> licenses) {
        if (licenses.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(String.join(" AND ", licenses));
    }
}
