/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

public interface ScannerService {
    /**
     * Scans the files in a given directory for licenses.
     *
     * @param directory location of the files
     * @return results of the scan
     */
    //TODO Not sure if we should scan files instead of an URI
    ScanResult scan(Path directory);

    /**
     * @return list of licenses detected in the content indicated by the URI
     */
    List<String> scanLicenses(URI uri);

    interface ScanResult {
        List<LicenseResult> getLicenses();
    }

    interface LicenseResult {
        String getExpression();

        int getScore();

        int getConfirmations();

        File getFile();

        int getStartLine();

        int getEndLine();
    }
}
