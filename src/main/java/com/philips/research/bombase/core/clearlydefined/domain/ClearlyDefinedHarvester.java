/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.philips.research.bombase.core.meta.AbstractRepoHarvester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClearlyDefinedHarvester extends AbstractRepoHarvester {
    private static final Set<String> SUPPORTED_TYPES = Set.of("npm", "gem", "pypi", "maven", "nuget", "github", "cargo", "deb", "composer", "cocoapods");

    @Autowired
    ClearlyDefinedHarvester(ClearlyDefinedClient client) {
        super(client::getPackageMetadata);
    }

    @Override
    protected boolean isSupportedType(String type) {
        return SUPPORTED_TYPES.contains(type);
    }
}
