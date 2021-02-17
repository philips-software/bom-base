/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class QueuedTaskRunner {
    @Async("taskExecutor")
        //@Transactional(propagation = Propagation.REQUIRES_NEW)
    void execute(Runnable task) {
        task.run();
    }
}
