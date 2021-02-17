/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined;

import com.philips.research.bombase.BusinessException;

public class ClearlyDefinedException extends BusinessException {
    public ClearlyDefinedException(String message) {
        super(message);
    }
}
