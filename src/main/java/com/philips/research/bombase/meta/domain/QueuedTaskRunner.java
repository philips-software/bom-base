/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.meta.domain;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class QueuedTaskRunner {
    /**
     * Queues tasks to run up to a configured maximum in parallel.
     *
     * @param task execution unit
     */
    @Async("taskExecutor")
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    void execute(Runnable task) {
        task.run();
    }
}
