/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta;

import com.philips.research.bombase.PackageUrl;

public class UnknownPackageException extends MetaException {
    public UnknownPackageException(PackageUrl purl) {
        super("Package '" + purl + "' is unknown");
    }
}
