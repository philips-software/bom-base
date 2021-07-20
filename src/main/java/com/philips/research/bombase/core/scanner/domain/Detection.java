/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;


import com.philips.research.bombase.core.scanner.ScannerService;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Evidence reference where a license was detected.
 * Keeps track of the location with the highest percentage match, and the number of matches found.
 */
public class Detection implements ScannerService.LicenseResult {
    private static final String[] UNLIKELY = {"test", "sample", "docs", "demo", "tutorial", "changelog"};

    private final String expression;

    private int score;
    private int confirmations;
    private File filePath;
    private int startLine;
    private int endLine;
    private boolean ignored;

    public Detection(String expression, int score, File file, int startLine, int endLine) {
        this.expression = expression;
        this.score = score;
        this.filePath = file;
        this.startLine = startLine;
        this.endLine = endLine;
        this.ignored = isSuspicious(file);
        this.confirmations = 1;
    }

    /**
     * Adds additional evidence to support this detection.
     */
    Detection merge(Detection detection) {
        if (!expression.equals(detection.expression)) {
            throw new IllegalArgumentException("Merging incompatible detections");
        }
        final var suspicious = isSuspicious(detection.filePath);
        if ((ignored && !suspicious) || detection.score > this.score ||
                (detection.score == this.score && detection.getLineCount() > this.getLineCount())) {
            this.score = detection.score;
            this.filePath = detection.filePath;
            this.startLine = detection.startLine;
            this.endLine = detection.endLine;
            this.ignored &= suspicious;
        }
        confirmations++;
        return this;
    }

    private boolean isSuspicious(File file) {
        final var lowercase = file.toString().toLowerCase();
        return Arrays.stream(UNLIKELY).anyMatch(lowercase::contains);
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getConfirmations() {
        return confirmations;
    }

    @Override
    public File getFile() {
        return filePath;
    }

    @Override
    public int getStartLine() {
        return startLine;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    public int getLineCount() {
        return endLine - startLine + 1;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Detection)) return false;
        Detection detection = (Detection) o;
        return Objects.equals(getExpression(), detection.getExpression());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getExpression());
    }
}
