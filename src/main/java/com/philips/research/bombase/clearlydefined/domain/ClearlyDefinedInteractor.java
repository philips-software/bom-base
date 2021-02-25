/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.clearlydefined.ClearlyDefinedService;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;
import com.philips.research.bombase.meta.Origin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class ClearlyDefinedInteractor implements ClearlyDefinedService {
    private static final Map<String, String> PROVIDERS = Map.of("maven", "mavencentral", "npm", "npmjs");
    private final MetaService service;
    private final ClearlyDefinedClient client;

    @Autowired
    public ClearlyDefinedInteractor(MetaService service) {
        this(service, new ClearlyDefinedClient());
    }

    ClearlyDefinedInteractor(MetaService service, ClearlyDefinedClient client) {
        this.service = service;
        this.client = client;
    }

    @Override
    public void init() {
        service.addListener(Origin.CLEARLY_DEFINED, this::onUpdated);
    }

    private Optional<Runnable> onUpdated(PackageUrl purl, Set<Field> fields, Map<Field, ?> values) {
        if (values.isEmpty()) {
            return Optional.of(() -> {
                final var provider = providerFor(purl);
                final var namespace = purl.getNamespace().orElse("");
                client.getPackageDefinition(purl.getType(), provider, namespace, purl.getName(), purl.getVersion())
                        .ifPresent(pkg -> {
                            final var update = new HashMap<Field, Object>();
                            copyIfPresent(pkg.getHomePage(), Field.HOME_PAGE, update);
                            copyIfPresent(pkg.getAttribution(), Field.ATTRIBUTION, update);
                            copyIfPresent(pkg.getDownloadLocation(), Field.DOWNLOAD_LOCATION, update);
                            copyIfPresent(pkg.getSourceLocation(), Field.SOURCE_LOCATION, update);
                            copyIfPresent(pkg.getDeclaredLicense(), Field.DECLARED_LICENSE, update);
                            copyIfPresent(pkg.getDetectedLicense(), Field.DETECTED_LICENSE, update);
                            copyIfPresent(pkg.getSha1(), Field.SHA1, update);
                            copyIfPresent(pkg.getSha256(), Field.SHA256, update);
                            service.update(Origin.CLEARLY_DEFINED, purl, update);
                        });
            });
        }
        return Optional.empty();
    }

    private void copyIfPresent(Optional<? extends Object> value, Field field, Map<Field,Object> map) {
       value.ifPresent(v -> map.put(field, v));
    }

    private void copyIfPresent(List<? extends Object> value, Field field, Map<Field,Object> map) {
        if (!value.isEmpty()) {
            map.put(field, value);
        }
    }

    private String providerFor(PackageUrl purl) {
        return PROVIDERS.getOrDefault(purl.getType(), purl.getType());
    }
}
