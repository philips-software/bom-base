/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.MetaException;

//TODO Not sure if this is a useful solution
public class UnknownPackageException extends MetaException {
    public UnknownPackageException(PackageURL purl) {
        super("Package '" + purl + "' is unknown");
    }
}
