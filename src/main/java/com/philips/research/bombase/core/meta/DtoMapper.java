/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta;

import com.philips.research.bombase.core.MetaService;
import com.philips.research.bombase.core.meta.registry.Package;

public abstract class DtoMapper {
   static MetaService.PackageDto toBaseDto(Package pkg) {
      final var dto = new MetaService.PackageDto();
      dto.purl = pkg.getPurl();
      dto.updated = pkg.getLastUpdated();
      return dto;
   }
}
