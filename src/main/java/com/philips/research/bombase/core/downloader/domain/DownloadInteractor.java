/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.downloader.domain;

import com.philips.research.bombase.core.downloader.DownloadService;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Function;

@Service
public class DownloadInteractor implements DownloadService {
    private static final Path CACHE_DIRECTORY = Path.of(System.getProperty("java.io.tmpdir"));
    private static final int CACHE_SIZE = 100;

    private final Downloader downloader;
    private final DownloadCache cache;

    public DownloadInteractor() {
        downloader = new Downloader()
                .register("", new AnonymousVcsHandler())
                .register("git", new GitVcsHandler());
        cache = new DownloadCache(downloader, CACHE_DIRECTORY, CACHE_SIZE);
    }

    @Override
    public <T> T download(URI location, Function<Path, T> operation) {
        final var directory = cache.obtain(location);
        try {
            return operation.apply(directory);
        } finally {
            cache.release(location);
        }
    }

    @PreDestroy
    void destroy() {
        cache.shutdown();
    }
}
