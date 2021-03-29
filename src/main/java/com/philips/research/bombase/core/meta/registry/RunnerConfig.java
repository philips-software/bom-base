/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class RunnerConfig {
    public static final String NAME = "TaskRunner";

    @Bean(name = NAME)
    public Executor threadPoolTaskExecutor() {
        final var executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(4);
        executor.setCorePoolSize(2);
        executor.setThreadGroupName(NAME);
        executor.setThreadNamePrefix("pool-");
        return executor;
    }
}
