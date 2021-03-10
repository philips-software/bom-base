/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class ClearlyDefinedListener implements MetaRegistry.PackageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ClearlyDefinedListener.class);
    private static final Map<String, String> PROVIDERS = Map.of("maven", "mavencentral", "npm", "npmjs");
    //TODO Replace by score provided with data
    private static final int SCORE = 70;

    private final ClearlyDefinedClient client;

    @Autowired
    public ClearlyDefinedListener() {
        this(new ClearlyDefinedClient());
    }

    ClearlyDefinedListener(ClearlyDefinedClient client) {
        this.client = client;
    }

    @Override
    public Optional<Consumer<PackageModifier>> onUpdated(PackageUrl purl, Set<Field> fields, Map<Field, ?> values) {
        if (!fields.isEmpty()) {
            return Optional.empty();
        }

        LOG.info("Created ClearlyDefined task for {}", purl);
        return Optional.of(modifier -> harvest(purl, modifier));
    }

    private void harvest(PackageUrl purl, PackageModifier modifier) {
        readPackage(purl).ifPresentOrElse(def -> {
            LOG.info("Updating {} from ClearlyDefined", purl);
            storeField(modifier, Field.SOURCE_LOCATION, def.getSourceLocation());
            storeField(modifier, Field.DOWNLOAD_LOCATION, def.getDownloadLocation());
            storeField(modifier, Field.HOME_PAGE, def.getHomepage());
//            storeField(modifier, Field.ATTRIBUTION, def.getAuthors());
            storeField(modifier, Field.DECLARED_LICENSE, def.getDeclaredLicense());
            storeField(modifier, Field.DETECTED_LICENSE, joinByAnd(def.getDetectedLicenses()));
        }, () -> LOG.info("No metadata for {} from ClearlyDefined", purl));
    }

    private <T> void storeField(PackageModifier modifier, Field field, Optional<T> value) {
        value.ifPresent(v -> modifier.update(field, SCORE, v));
    }

    private <T> void storeField(PackageModifier modifier, Field field, List<T> values) {
        if (!values.isEmpty()) {
            modifier.update(field, SCORE, values);
        }
    }

    private Optional<PackageDefinition> readPackage(PackageUrl purl) {
        //TODO What about multiple provides for a single type?
        final var provider = PROVIDERS.getOrDefault(purl.getType(), purl.getType());
        final var namespace = purl.getNamespace().orElse("");

        return client.getPackageDefinition(purl.getType(), provider, namespace, purl.getName(), purl.getVersion());
    }

    Optional<String> joinByAnd(List<String> licenses) {
        if (licenses.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(String.join(" AND ", licenses));
    }
}
