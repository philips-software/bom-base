/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.downloader.domain;

import java.net.URI;
import java.nio.file.Path;

/**
 * Version control handler API.
 */
interface VcsHandler {
    /**
     * Downloads package sources.
     *
     * @param directory target directory to store the sources
     * @param location  download location using the format {@code <transport>://<host_name>[/<path_to_repository>][@<revision_tag_or_branch>][#<sub_path>]}
     * @return base directory of the download result
     */
    Path download(Path directory, URI location);
}
