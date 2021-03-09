/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.philips.research.bombase.BusinessException;

public class MetaException extends BusinessException {
    public MetaException(String message) {
        super(message);
    }
}

