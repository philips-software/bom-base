/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta;

import java.net.URI;

public class UnknownPackageException extends MetaException {
    public UnknownPackageException(URI pkg) {
        super("Package '" + pkg + "' is unknown");
    }
}
