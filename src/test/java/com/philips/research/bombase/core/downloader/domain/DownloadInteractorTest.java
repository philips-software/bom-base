/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.downloader.domain;

import com.philips.research.bombase.core.downloader.DownloadException;
import com.philips.research.bombase.core.downloader.DownloadService;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DownloadInteractorTest {
    private static final URI FILE_URI = Path.of("src", "test", "resources", "downloader", "plain.txt").toUri();

    final DownloadService interactor = new DownloadInteractor();

    @Test
    void downloadsFromUri() {
        final var found = interactor.download(FILE_URI, path -> path.toFile().exists());

        assertThat(found).isTrue();
    }

    @Test
    void throws_downloadFails() {
        //noinspection ConstantConditions
        assertThatThrownBy(() -> interactor.download(Path.of("unknown.file").toUri(), null))
                .isInstanceOf(DownloadException.class)
                .hasMessageContaining("File download failed");
    }
}
