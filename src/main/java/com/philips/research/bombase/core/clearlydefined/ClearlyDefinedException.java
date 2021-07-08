/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined;

import com.philips.research.bombase.core.BusinessException;

public class ClearlyDefinedException extends BusinessException {
    public ClearlyDefinedException(String message) {
        super(message);
    }

    public ClearlyDefinedException(String message, Throwable cause) {
        super(message, cause);
    }
}
