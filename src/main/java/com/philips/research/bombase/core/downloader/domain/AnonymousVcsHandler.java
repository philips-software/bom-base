/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.downloader.domain;

import com.philips.research.bombase.core.downloader.DownloadException;
import com.philips.research.bombase.core.support.ShellCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Download handler for file and internet resources.
 */
class AnonymousVcsHandler implements VcsHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AnonymousVcsHandler.class);
    private static final Duration MAX_EXTRACT_DURATION = Duration.ofMinutes(10);

    @Override
    public Path download(Path directory, URI location) {
        validateDirectory(directory);
        copyFile(target(directory, location), location);
        final var path = extractArchives(directory);
        final @NullOr String fragment = location.getFragment();

        return (fragment != null) ? path.resolve(fragment) : path;
    }

    private void validateDirectory(Path directory) {
        final var file = directory.toFile();
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }
    }

    private File target(Path directory, URI location) {
        final var filename = filenameFor(location);
        return directory.resolve(filename).toFile();
    }

    private void copyFile(File target, URI fromUri) {
        LOG.info("Download file from {} to {}", fromUri, target);
        try (FileOutputStream out = new FileOutputStream(target)) {
            ReadableByteChannel inChannel = Channels.newChannel(openStream(fromUri));
            FileChannel outChannel = out.getChannel();
            outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
            inChannel.close();
        } catch (IOException e) {
            throw new DownloadException("File download failed from " + fromUri, e);
        }
    }

    private String filenameFor(URI uri) {
        if ("file".equals(uri.getScheme())) {
            return new File(uri.getSchemeSpecificPart()).getName();
        }

        final var path = uri.getPath();
        final var pos = path.lastIndexOf('/');
        return (pos >= 0) ? path.substring(pos + 1) : path;
    }

    private Path extractArchives(Path directory) {
        //noinspection SpellCheckingInspection
        final var baseDir = directory.toFile();
        new ShellCommand("extractcode").setDirectory(baseDir)
                .setTimeout(MAX_EXTRACT_DURATION)
                .execute("--verbose", "--shallow", "--replace-originals", ".");
        //noinspection ConstantConditions
        return Arrays.stream(baseDir.listFiles())
                .filter(File::isDirectory)
                .findFirst().orElse(baseDir)
                .toPath();
    }

    /**
     * Workaround to redirect from HTTP to HTTPS.
     *
     * @see <a href="https://stackoverflow.com/questions/1884230/httpurlconnection-doesnt-follow-redirect-from-http-to-https">This explanation</a>
     */
    private InputStream openStream(URI uri) throws IOException {
        final var connection = uri.toURL().openConnection();
        if (!(connection instanceof HttpURLConnection)) {
            return connection.getInputStream();
        }

        Map<URI, Integer> visited = new HashMap<>();
        var urlConnection = (HttpURLConnection) connection;
        while (true) {
            final int times = visited.compute(uri, (key, count) -> count == null ? 1 : count + 1);
            if (times > 3) {
                throw new IOException("Aborted: Stuck in redirect loop");
            }

            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setInstanceFollowRedirects(false);   // We handle redirects ourselves
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0...");
            if (urlConnection.getResponseCode() != 301) {
                return urlConnection.getInputStream();
            }

            uri = redirection(uri, urlConnection);
            urlConnection = (HttpURLConnection) uri.toURL().openConnection();
        }
    }

    private URI redirection(URI uri, HttpURLConnection conn) {
        final var location = conn.getHeaderField("Location");
        final var url = URLDecoder.decode(location, StandardCharsets.UTF_8);
        return uri.resolve(url);
    }
}
