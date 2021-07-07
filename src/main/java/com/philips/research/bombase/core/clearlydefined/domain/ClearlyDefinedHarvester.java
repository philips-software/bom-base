/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class ClearlyDefinedHarvester implements MetaRegistry.PackageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ClearlyDefinedHarvester.class);
    //TODO Is this even a realistic score?
    private static final int MAX_SCORE = 70;
    private static final Set<String> SUPPORTED_TYPES = Set.of("npm", "gem", "pypi", "maven", "nuget", "github", "cargo", "deb", "composer", "cocoapods");

    private final ClearlyDefinedClient client;

    @Autowired
    public ClearlyDefinedHarvester() {
        this(new ClearlyDefinedClient());
    }

    ClearlyDefinedHarvester(ClearlyDefinedClient client) {
        this.client = client;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, ?> values) {
        if (!SUPPORTED_TYPES.contains(purl.getType()) || !updated.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pkg -> harvest(purl, pkg));
    }

    private void harvest(PackageURL purl, PackageAttributeEditor pkg) {
        client.getPackageDefinition(purl).ifPresentOrElse(def -> {
            int metaScore = absoluteScore(def.getDescribedScore());
            int licenseScore = absoluteScore(def.getLicensedScore());
            storeField(pkg, Field.TITLE, metaScore, def.getTitle());
            storeField(pkg, Field.SOURCE_LOCATION, metaScore, def.getSourceLocation());
            storeField(pkg, Field.DOWNLOAD_LOCATION, metaScore, def.getDownloadLocation());
            storeField(pkg, Field.HOME_PAGE, metaScore, def.getHomepage());
            storeField(pkg, Field.ATTRIBUTION, metaScore, def.getAuthors());
            storeField(pkg, Field.DECLARED_LICENSE, metaScore, def.getDeclaredLicense());
            storeField(pkg, Field.DETECTED_LICENSES, licenseScore, def.getDetectedLicenses());
            storeField(pkg, Field.SHA1, metaScore, def.getSha1());
            storeField(pkg, Field.SHA256, metaScore, def.getSha256());
        }, () -> LOG.info("No metadata for {}", purl));
    }

    private int absoluteScore(int score) {
        return (int) Math.round(MAX_SCORE * score / 100.0);
    }

    private <T> void storeField(PackageAttributeEditor pkg, Field field, int score, Optional<T> value) {
        value.ifPresent(v -> pkg.update(field, score, v));
    }
}
