/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.downloader.domain;

import com.philips.research.bombase.core.downloader.DownloadException;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnonymousVcsHandlerTest extends VcsHandlerTestBase {
    private static final Path RESOURCES_PATH = Path.of("src", "test", "resources", "downloader");
    private static final String ARCHIVE = "archive.zip";
    private static final String PLAIN_FILE = "plain.txt";
    private final VcsHandler handler = new AnonymousVcsHandler();

    @Test
    void throws_targetDirectoryDoesNotExist() {
        assertThatThrownBy(() -> handler.download(Path.of("DoesNotExist"), RESOURCES_PATH.toUri()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not a directory");
    }

    @Test
    void downloadsFromFileURI() {
        final var dir = handler.download(tempDir, RESOURCES_PATH.resolve(PLAIN_FILE).toUri());

        assertThat(tempDir.resolve(PLAIN_FILE).toFile()).exists();
        assertThat(dir).isEqualTo(tempDir);
    }

    @Test
    void throws_nonExistingFile() {
        assertThatThrownBy(() -> handler.download(tempDir, Path.of("not_a_file").toUri()))
                .isInstanceOf(DownloadException.class)
                .hasMessageContaining("File download");
    }

    @Test
    void downloadsFromWebURL() {
        handler.download(tempDir, URI.create("https://example.com/index.html"));

        assertThat(tempDir.resolve("index.html").toFile()).exists();
    }

    @Test
    void extractsArchivesAfterDownload() {
        final var dir = handler.download(tempDir, RESOURCES_PATH.resolve(ARCHIVE).toUri());

        assertThat(tempDir.resolve(ARCHIVE).resolve("archive").resolve("archived.txt").toFile()).exists();
        assertThat(dir).isEqualTo(tempDir.resolve(ARCHIVE));
    }

    @Test
    void indicatesPathFromLocation() {
        final var dir = handler.download(tempDir, RESOURCES_PATH.toUri().resolve("#sample%2Fpath"));

        assertThat(dir).isEqualTo(tempDir.resolve("sample").resolve("path"));
    }
}
