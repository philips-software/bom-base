/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi;

import com.philips.research.bombase.core.BusinessException;

public class PyPiException extends BusinessException {
    public PyPiException(String message) {
        super(message);
    }

    public PyPiException(String message, Throwable cause) {
        super(message, cause);
    }
}
