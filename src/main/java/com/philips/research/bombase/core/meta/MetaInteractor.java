/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.ConfigProperties;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.UnknownPackageException;
import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedHarvester;
import com.philips.research.bombase.core.license_cleaner.domain.LicenseCleaner;
import com.philips.research.bombase.core.maven.domain.MavenHarvester;
import com.philips.research.bombase.core.meta.registry.Field;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import com.philips.research.bombase.core.meta.registry.Trust;
import com.philips.research.bombase.core.npm.domain.NpmHarvester;
import com.philips.research.bombase.core.nuget.domain.NugetHarvester;
import com.philips.research.bombase.core.pypi.domain.PyPiHarvester;
import com.philips.research.bombase.core.source_scan.domain.SourceLicensesHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(MetaInteractor.class);

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
        final var properties = context.getBean(ConfigProperties.class);

        if (properties.harvestClearlyDefined()) {
            installListener(ClearlyDefinedHarvester.class);
        }
        installListener(PyPiHarvester.class);
        installListener(NpmHarvester.class);
        installListener(NugetHarvester.class);
        installListener(MavenHarvester.class);
        installListener(LicenseCleaner.class);
        if (properties.isScanLicenses()) {
            installListener(SourceLicensesHarvester.class);
        }
    }

    private void installListener(Class<? extends MetaRegistry.PackageListener> listener) {
        registry.addListener(context.getBean(listener));
    }

    @Override
    public void createPackage(PackageURL purl) {
        registry.edit(purl, pkg -> {
        });
    }

    @Override
    public Map<String, AttributeDto> getAttributes(PackageURL purl) {
        final var values = new HashMap<String, AttributeDto>();
        registry.getAttributeValues(purl)
                .orElseThrow(() -> new UnknownPackageException(purl))
                .forEach((field, attribute) -> values.put(field.name().toLowerCase(), DtoMapper.toDto(attribute)));
        return values;
    }

    @Override
    public Map<String, AttributeDto> setAttributes(PackageURL purl, Map<String, @NullOr Object> values) {
        registry.edit(purl, pkg -> values.forEach((key, value) -> {
            final var field = Field.valueOf(Field.class, key.toUpperCase());
            pkg.update(field, Trust.TRUTH, value);
        }));
        return getAttributes(purl);
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
