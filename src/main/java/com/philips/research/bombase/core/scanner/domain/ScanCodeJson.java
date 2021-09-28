/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.scanner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.philips.research.bombase.core.scanner.ScannerService;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ScanCode Toolkit JSON result file mapping.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ScanCodeJson implements ScannerService.ScanResult {
    @JsonProperty("files")
    final List<FileJson> files = new ArrayList<>();

    @Override
    public List<ScannerService.LicenseResult> getLicenses() {
        final var map = new HashMap<String, Detection>();

        files.stream()
                .flatMap(file -> file.getDetections().stream())
                .forEach(det -> map.compute(det.getExpression(), (expr, prev) -> (prev != null) ? prev.merge(det) : det));

        return new ArrayList<>(map.values());
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class FileJson {
    @JsonProperty("licenses")
    final List<LicenseJson> licenses = new ArrayList<>();
    @JsonProperty("license_expressions")
    final List<String> expressions = new ArrayList<>();
    private final Map<String, LicenseJson> licenseDictionary = new HashMap<>();
    @JsonProperty("path")
    String path = "";

    List<Detection> getDetections() {
        final var dictionary = LicenseJson.buildDictionary(licenses);
        final var converter = new ExpressionConverter(dictionary);

        return expressions.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    /**
     * Extracts and collects license details from a key-based expression.
     */
    private class ExpressionConverter {
        private final Map<String, LicenseJson> dictionary;

        private int startLine;
        private int endLine;
        private int score;

        public ExpressionConverter(Map<String, LicenseJson> dictionary) {
            this.dictionary = dictionary;
        }

        Detection convert(String expression) {
            startLine = Integer.MAX_VALUE;
            endLine = 0;
            score = 100;

            final var converted = new StringBuilder();
            var key = new StringBuilder();
            for (var ch : expression.toCharArray()) {
                if (Character.isSpaceChar(ch) || ch == '(' || ch == ')') {
                    final var spdx = processKey(key.toString());
                    converted.append(spdx);
                    converted.append(ch);
                    key.setLength(0);
                } else {
                    key.append(ch);
                }
            }
            converted.append(processKey(key.toString()));

            return new Detection(converted.toString(), score, new File(path), startLine, endLine);
        }

        String processKey(String key) {
            final var lic = dictionary.get(key);
            if (lic == null) {
                return key;
            }

            score = Math.min(score, (int) lic.effectiveScore());
            startLine = Math.min(startLine, lic.startLine);
            endLine = Math.max(endLine, lic.endLine);
            return lic.getIdentifier();
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class LicenseJson {
    @JsonProperty("key")
    @NullOr String key;
    @JsonProperty("score")
    double score;
    @JsonProperty("start_line")
    int startLine;
    @JsonProperty("end_line")
    int endLine;
    @JsonProperty("spdx_license_key")
    @NullOr String spdx;

    static Map<String, LicenseJson> buildDictionary(List<LicenseJson> licenses) {
        final var dictionary = new HashMap<String, LicenseJson>();
        for (var license : licenses) {
            final var existing = dictionary.get(license.key);
            if (existing == null || license.score > existing.score
                    || (license.score == existing.score && license.lines() > existing.lines())) {
                dictionary.put(license.key, license);
            }
        }
        return dictionary;
    }

    String getIdentifier() {
        assert spdx != null || key != null;
        return (spdx != null) ? spdx : key;
    }

    double effectiveScore() {
        return getIdentifier().contains("LicenseRef")
                ? 0.8f * score
                : score;
    }

    public int lines() {
        return endLine - startLine + 1;
    }
}

