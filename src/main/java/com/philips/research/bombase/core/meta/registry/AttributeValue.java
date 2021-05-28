/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import java.util.Optional;

public interface AttributeValue<T> {
    Optional<T> getValue();

    int getScore();

    Optional<T> getAltValue();

    int getAltScore();
}
