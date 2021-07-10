/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.pypi.PyPiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PyPiClientTest {
    private static final int PORT = 1082;
    private static final String VERSION = "1.2";
    private static final PackageURL PURL = createPurl("pkg:pypi/name@" + VERSION);
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Summary";
    private static final String AUTHOR = "Author";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String DECLARED_LICENSE = "Declared";
    private static final String DOWNLOAD_LOCATION = "https://example.com/download";
    private static final String SHA256 = "Sha256";

    private final PyPiClient client = new PyPiClient(URI.create("http://localhost:" + PORT));
    private final MockWebServer mockServer = new MockWebServer();

    static PackageURL createPurl(String purl) {
        try {
            return new PackageURL(purl);
        } catch (MalformedPackageURLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        mockServer.start(PORT);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void skipsUndefinedPackage() {
        mockServer.enqueue(new MockResponse().setResponseCode(404));

        assertThat(client.getPackageMetadata(PURL)).isEmpty();
    }

    @Test
    void getsMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("info", new JSONObject()
                        .put("author", AUTHOR)
                        .put("name", TITLE)
                        .put("summary", DESCRIPTION)
                        .put("home_page", HOMEPAGE)
                        .put("license", DECLARED_LICENSE))
                .put("urls", new JSONArray()
                        .put(new JSONObject()
                                .put("packagetype", "irrelevant"))
                        .put(new JSONObject()
                                .put("packagetype", "sdist")
                                .put("url", SOURCE_LOCATION))
                        .put(new JSONObject()
                                .put("packagetype", "bdist_wheel")
                                .put("url", DOWNLOAD_LOCATION)
                                .put("digests", new JSONObject()
                                        .put("sha256", SHA256))))
                .toString()));
        final var release = client.getPackageMetadata(PURL).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/pypi/name/" + VERSION + "/json");
        assertThat(release.getTitle()).contains(TITLE);
        assertThat(release.getDescription()).contains(DESCRIPTION);
        assertThat(release.getAuthors()).contains(List.of(AUTHOR));
        assertThat(release.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(release.getSourceLocation()).contains(SOURCE_LOCATION);
        assertThat(release.getDeclaredLicense()).contains(DECLARED_LICENSE);
        assertThat(release.getDownloadLocation()).contains(URI.create(DOWNLOAD_LOCATION));
        assertThat(release.getSha256()).contains(SHA256);
    }

    @Test
    void acceptsEmptyMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("info", new JSONObject())
                .put("releases", new JSONObject())
                .toString()));

        final var release = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(release).isInstanceOf(PackageMetadata.class);
    }


    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new PyPiClient(URI.create("http://localhost:1234"));

        assertThatThrownBy(() -> serverlessClient.getPackageMetadata(PURL))
                .isInstanceOf(PyPiException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.getPackageMetadata(PURL))
                .isInstanceOf(PyPiException.class)
                .hasMessageContaining("status 500");
    }
}
