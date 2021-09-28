/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven.domain;

import com.philips.research.bombase.core.meta.AbstractRepoHarvester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MavenHarvester extends AbstractRepoHarvester {
    @Autowired
    MavenHarvester(MavenClient client) {
        super(client::getPackageMetadata);
    }

    @Override
    protected boolean isSupportedType(String type) {
        return type.equals("maven");
    }
}
