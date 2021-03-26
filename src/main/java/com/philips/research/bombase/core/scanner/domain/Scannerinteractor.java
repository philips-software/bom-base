/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import com.philips.research.bombase.core.scanner.ScannerService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class Scannerinteractor implements ScannerService {
    @Override
    public ScanResult scan(Path directory) {
        return null;
    }
}
