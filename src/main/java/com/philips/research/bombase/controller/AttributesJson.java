/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.philips.research.bombase.core.MetaService.AttributeDto;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributesJson {
    Map<String, AttributeJson> attributes = new HashMap<>();

    @SuppressWarnings("unused")
    AttributesJson() {
    }

    AttributesJson(Map<String, AttributeDto> attributes) {
        this.attributes = attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new AttributeJson(e.getValue())));
    }

    static class AttributeJson {
        int score;
        @NullOr Object value;
        @NullOr Integer altScore;
        @NullOr Object altValue;

        AttributeJson(AttributeDto dto) {
            this.score = dto.score;
            this.value = dto.value;
            this.altScore = (dto.altValue != null) ? dto.altScore : null;
            this.altValue = dto.altValue;
        }
    }
}
