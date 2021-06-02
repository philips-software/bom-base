/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.philips.research.bombase.core.MetaService.AttributeDto;
import com.philips.research.bombase.core.MetaService.PackageDto;
import com.philips.research.bombase.core.meta.registry.AttributeValue;
import com.philips.research.bombase.core.meta.registry.Package;

public abstract class DtoMapper {
    static PackageDto toBaseDto(Package pkg) {
        final var dto = new PackageDto();
        dto.purl = pkg.getPurl();
        dto.updated = pkg.getLastUpdated();
        return dto;
    }

    static AttributeDto toDto(AttributeValue<?> value) {
        final var dto = new AttributeDto();
        dto.score = value.getScore();
        value.getValue().ifPresent(v -> dto.value = v);
        dto.altScore = value.getAltScore();
        value.getAltValue().ifPresent(v -> dto.altValue = v);
        return dto;
    }
}
