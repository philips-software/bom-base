/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bom-base")
public class ConfigProperties {
    private boolean scanLicenses = true;
    private boolean useClearlyDefined = true;

    public boolean isScanLicenses() {
        return scanLicenses;
    }
    
    public boolean isUseClearlyDefined() {
        return useClearlyDefined;
    }

    public ConfigProperties setScanLicenses(boolean scanLicenses) {
        this.scanLicenses = scanLicenses;
        return this;
    }
    
    public ConfigProperties setUseClearlyDefined(boolean useClearlyDefined) {
        this.useClearlyDefined = useClearlyDefined;
        return this;
    }
}
