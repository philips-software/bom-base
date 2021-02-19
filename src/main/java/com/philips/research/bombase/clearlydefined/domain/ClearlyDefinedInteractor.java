/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import com.philips.research.bombase.PackageUrl;
import com.philips.research.bombase.clearlydefined.ClearlyDefinedService;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        service.addListener(this::onUpdated);
    }

    private Optional<Runnable> onUpdated(PackageUrl purl, Set<Field> fields, Map<Field, ?> values) {
        if (values.isEmpty()) {
            return Optional.of(() -> {
                final var provider = providerFor(purl);
                final var namespace = purl.getNamespace().orElse("");
                client.getPackageDefinition(purl.getType(), provider, namespace, purl.getName(), purl.getVersion())
                        .ifPresent(pkg -> {
                            final var update = new HashMap<Field, Object>();
                            pkg.getSourceLocation().ifPresent(l -> update.put(Field.SOURCE_LOCATION, l));
                            service.update(purl, update);
                        });
            });
        }
        return Optional.empty();
    }

    private String providerFor(PackageUrl purl) {
        return PROVIDERS.getOrDefault(purl.getType(), purl.getType());
    }
}
