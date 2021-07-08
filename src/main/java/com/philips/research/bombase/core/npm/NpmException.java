/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm;

import com.philips.research.bombase.core.BusinessException;

public class NpmException extends BusinessException {
    public NpmException(String message) {
        super(message);
    }

    public NpmException(String message, Throwable cause) {
        super(message, cause);
    }
}
