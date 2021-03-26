/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.downloader.domain;

import com.philips.research.bombase.core.downloader.DownloadException;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring component delegating download of package source code from a provided URI to registered download handlers.
 */
class Downloader {
    private final Map<String, VcsHandler> registry = new HashMap<>();

    Downloader() {
        register("", new AnonymousVcsHandler());
        register("git", new GitVcsHandler());
    }

    /**
     * Registers a download handler for a single tool.
     *
     * @param tool tool identifier
     */
    Downloader register(String tool, VcsHandler handler) {
        registry.put(tool, handler);
        return this;
    }

    /**
     * Downloads the source of a package from the provided location.
     *
     * @param directory
     * @param location  download location using the format {@code <vcs_tool>+<transport>://<host_name>[/<path_to_repository>][@<revision_tag_or_branch>][#<sub_path>]}
     * @return path to the downloaded sources
     * @throws DownloadException if downloading failed or no handler matches the location.
     */
    Path download(Path directory, URI location) {
        final var handler = validHandler(location);
        final var uri = downloadUri(location);

        return handler.download(directory, uri);
    }

    private VcsHandler validHandler(URI location) {
        final var scheme = location.getScheme();
        final var pos = scheme.indexOf('+');
        final var tool = (pos >= 0) ? scheme.substring(0, pos) : "";

        final var handler = registry.get(tool);
        if (handler == null) {
            throw new DownloadException("No handler registered for '" + tool + "'");
        }
        return handler;
    }

    private URI downloadUri(URI location) {
        final var pos = location.getScheme().indexOf('+');
        if (pos >= 0) {
            return URI.create(location.toString().substring(pos + 1));
        }

        return location;
    }
}
