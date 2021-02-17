/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import com.philips.research.bombase.clearlydefined.ClearlyDefinedService;
import com.philips.research.bombase.meta.Field;
import com.philips.research.bombase.meta.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ClearlyDefinedInteractor implements ClearlyDefinedService {
    private static final Map<String,String> PROVIDERS = Map.of("maven", "mavencentral", "npm", "npmjs");
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

    private Optional<Runnable> onUpdated(URI purl, Set<Field> fields, Map<Field, ?> values) {
        if (fields.contains(Field.NAME)) {
            return Optional.of(() -> {
                final String type = (String) values.get(Field.TYPE);
                final String provider = PROVIDERS.getOrDefault(type, type);
                final String name = (String) values.get(Field.NAME);
                final String version = (String) values.get(Field.VERSION);
                client.getPackageDefinition(type, provider, "", name, version)
                        .ifPresent(pkg -> {
                            final var update = new HashMap<Field, Object>();
                            pkg.getSourceLocation().ifPresent(l -> update.put(Field.SOURCE_LOCATION, l));
                            service.update(purl, update);
                        });
            });
        }
        return Optional.empty();
    }
}
