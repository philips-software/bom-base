/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase;

import com.philips.research.bombase.clearlydefined.ClearlyDefinedService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MetabaseApplication {
    public static void main(String[] args) {
        final var context = SpringApplication.run(MetabaseApplication.class, args);

        startHarvesters(context);
    }

    private static void startHarvesters(org.springframework.context.ConfigurableApplicationContext context) {
        context.getBean(ClearlyDefinedService.class).init();
    }
}
