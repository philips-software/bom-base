/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.source_scan;

import com.philips.research.bombase.core.BusinessException;

public class SourceScanException extends BusinessException {
    public SourceScanException(String message) {
        super(message);
    }

    public SourceScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
