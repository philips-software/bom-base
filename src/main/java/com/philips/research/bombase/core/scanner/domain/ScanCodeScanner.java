/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.research.bombase.core.scanner.ScannerException;
import com.philips.research.bombase.core.scanner.ScannerService.ScanResult;
import com.philips.research.bombase.core.support.ShellCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Spring component for detecting licenses using the ScanCode Toolkit detector.
 *
 * @see <a href="https://github.com/nexB/scancode-toolkit">ScanCode Toolkit</a>
 */
class ScanCodeScanner {
    private static final String RESULT_FILE = "scancode.json";
    private static final Duration MAX_SCAN_DURATION = Duration.ofMinutes(30);
    private static final int SCORE_THRESHOLD = 50;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    ScanResult scan(Path directory) {
        new ShellCommand("scancode")
                .setDirectory(directory.toFile())
                .setTimeout(MAX_SCAN_DURATION)
                .execute("--license", "-n2", "--verbose", "--timeout=" + MAX_SCAN_DURATION.toSeconds(), "--only-findings",
                        "--license-score", SCORE_THRESHOLD, "--strip-root", "--ignore", "test*", "--ignore", RESULT_FILE,
                        "--json-pp", RESULT_FILE, ".");
        return readResult(directory.resolve(RESULT_FILE).toFile());
    }

    private ScanResult readResult(File file) {
        try {
            return MAPPER.readValue(file, ScanCodeJson.class);
        } catch (IOException e) {
            throw new ScannerException("Failed to read scan results", e);
        }
    }
}
