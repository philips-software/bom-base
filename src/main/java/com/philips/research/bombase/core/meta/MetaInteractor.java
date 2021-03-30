/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedListener;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.source_scan.domain.SourceScanListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetaInteractor implements MetaService {
    private final MetaRegistry registry;
    private final MetaStore store;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ApplicationContext context;

    public MetaInteractor(MetaRegistry registry, MetaStore store) {
        this.registry = registry;
        this.store = store;
    }

    @PostConstruct
    void init() {
        registry.addListener(context.getBean(ClearlyDefinedListener.class));
        registry.addListener(context.getBean(SourceScanListener.class));
    }

    @Override
    public Map<String, Object> getAttributes(PackageURL purl) {
        final var values = new HashMap<String, Object>();
        registry.edit(purl, pkg -> pkg.getValues().forEach((field, value) -> {
            if (value != null) {
                values.put(field.name().toLowerCase(), value);
            }
        }));
        return values;
    }

    @Override
    public void setAttributes(PackageURL purl, Map<String, @NullOr Object> values) {
        registry.edit(purl, pkg -> values.forEach((key, value) -> {
            final var field = Field.valueOf(Field.class, key.toUpperCase());
            pkg.update(field, 100, value);
        }));
    }

    @Override
    public List<PackageDto> latestScans() {
        return store.latestScans(100).stream()
                .map(DtoMapper::toBaseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PackageDto> search(String type, String namespace, String name, String version) {
        return store.findPackages(type, namespace, name, version).stream()
                .map(DtoMapper::toBaseDto)
                .collect(Collectors.toList());
    }
}
