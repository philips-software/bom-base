/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import java.util.List;

class ResultJson<T> {
    List<T> results;

    public ResultJson(List<T> results) {
        this.results = results;
    }
}
