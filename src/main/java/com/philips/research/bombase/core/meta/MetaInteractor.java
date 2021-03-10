/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.clearlydefined.domain.ClearlyDefinedListener;
import com.philips.research.bombase.core.meta.registry.MetaRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Map;

@Service
public class MetaInteractor implements MetaService {
    private final MetaRegistry registry;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ApplicationContext context;

    public MetaInteractor(MetaRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    void init() {
        registry.addListener(context.getBean(ClearlyDefinedListener.class));
    }

    @Override
    public void update(URI purl, Map<String, Object> values) {
        //TODO Needs to be tested...
        try {
            PackageURL pkgUrl = new PackageURL(purl.toASCIIString());
            registry.edit(pkgUrl, pkg -> {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Not a valid package URL: " + purl, e);
        }
    }

    @Override
    public Map<String, Object> valuesOf(URI purl) {
        return null;
    }
}
