/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.MetaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class QueuedTaskRunner {
    private static final Logger LOG = LoggerFactory.getLogger(QueuedTaskRunner.class);

    private final MetaStore store;

    public QueuedTaskRunner(MetaStore store) {
        this.store = store;
    }

    /**
     * Queues tasks to run up to a configured maximum in parallel.
     *
     * @param task execution unit
     */
    @Async(RunnerConfig.NAME)
    public
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    void execute(PackageURL purl, Consumer<PackageAttributeEditor> task, Consumer<PackageAttributeEditor> callback) {
        store.findPackage(purl).ifPresent(pkg -> {
            final var editor = new PackageAttributeEditor(pkg);
            task.accept(editor);
            callback.accept(editor);
        });
    }
}
