/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@Async
public class RunnerConfig {
    @Bean(name = "taskRunner")
    public Executor threadPoolTaskExecutor() {
        final var executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(2);
        executor.setThreadGroupName("taskRunner");
        executor.setThreadNamePrefix("task");
        return executor;
    }
}
