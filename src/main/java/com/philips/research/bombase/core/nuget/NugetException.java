/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.nuget;

import com.philips.research.bombase.core.BusinessException;

public class NugetException extends BusinessException {
    public NugetException(String message) {
        super(message);
    }

    public NugetException(String message, Throwable cause) {
        super(message, cause);
    }
}
