/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven;

import com.philips.research.bombase.core.BusinessException;

public class MavenException extends BusinessException {
    public MavenException(String message) {
        super(message);
    }

    public MavenException(String message, Throwable cause) {
        super(message, cause);
    }
}
