/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.PackageAttributeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Base class for package repository harvesters that collect all metadata per package.
 */
public abstract class AbstractRepoHarvester implements MetaRegistry.PackageListener {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRepoHarvester.class);

    private final Client client;

    public AbstractRepoHarvester(Client client) {
        this.client = client;
    }

    @Override
    public Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, ?> values) {
        if (!isSupportedType(purl.getType()) || !updated.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pkg -> harvest(purl, pkg));
    }

    /**
     * @param type Type field of the package URL
     * @return true if this harvester supports the given type
     */
    abstract boolean isSupportedType(String type);

    private void harvest(PackageURL purl, PackageAttributeEditor editor) {
        try {
            client.read(purl).ifPresentOrElse(def -> {
                def.getTitle().ifPresent(title -> editor.update(Field.TITLE, def.score(Field.TITLE), title));
                def.getSourceLocation().ifPresent(url -> editor.update(Field.SOURCE_LOCATION, def.score(Field.SOURCE_LOCATION), url));
                def.getDownloadLocation().ifPresent(url -> editor.update(Field.DOWNLOAD_LOCATION, def.score(Field.DOWNLOAD_LOCATION), url));
                def.getHomepage().ifPresent(url -> editor.update(Field.HOME_PAGE, def.score(Field.HOME_PAGE), url));
                def.getAuthors().ifPresent(list -> editor.update(Field.ATTRIBUTION, def.score(Field.ATTRIBUTION), list));
                def.getDeclaredLicense().ifPresent(license -> editor.update(Field.DECLARED_LICENSE, def.score(Field.DECLARED_LICENSE), license));
                def.getDetectedLicenses().ifPresent(list -> editor.update(Field.DETECTED_LICENSES, def.score(Field.DETECTED_LICENSES), list));
                def.getSha1().ifPresent(sha -> editor.update(Field.SHA1, def.score(Field.SHA1), sha));
                def.getSha256().ifPresent(sha -> editor.update(Field.SHA256, def.score(Field.SHA256), sha));
            }, () -> LOG.info("No metadata for {}", purl));
        } catch (Exception e) {
            throw new MetaException("Failed to harvest " + purl, e);
        }
    }

    @FunctionalInterface
    interface Client {
        Optional<PackageMetadata> read(PackageURL purl);
    }
}

