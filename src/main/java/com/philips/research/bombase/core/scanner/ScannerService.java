/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface ScannerService {
    /**
     * Scans the files in a given directory for licenses.
     *
     * @param directory location of the files
     * @return results of the scan
     */
    ScanResult scan(Path directory);

    interface ScanResult {
       List<LicenseResult> getLicenses();
    }

    interface LicenseResult {
       String getExpression();
       int getConfirmations();
       File getFile();
       int getStartLine();
       int getEndLine();
    }
}
