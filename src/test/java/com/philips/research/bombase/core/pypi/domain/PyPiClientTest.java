/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.pypi.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PyPiClientTest {
    private static final int PORT = 1080;
    private static final String TYPE = "type";
    private static final String NAMESPACE = "Namespace";
    private static final String NAME = "Name";
    private static final String VERSION = "Release";
    private static final PackageURL PURL = createPurl(String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION));
    private static final String SUMMARY = "Summary";
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String DECLARED_LICENSE = "Declared";

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
    void skipsUndefinedPackage() throws Exception {
        mockServer.enqueue(new MockResponse().setResponseCode(404));

        assertThat(client.getRelease(PURL)).isEmpty();
    }

    @Test
    void getsMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("info", new JSONObject()
                        .put("summary", SUMMARY)
                        .put("home_page", HOMEPAGE)
                        .put("license", DECLARED_LICENSE))
                .put("releases", new JSONObject()
                        .put("other", new JSONArray())
                        .put(VERSION, new JSONArray()
                                .put(new JSONObject()
                                        .put("packagetype", "irrelevant"))
                                .put(new JSONObject()
                                        .put("packagetype", "sdist")
                                        .put("url", SOURCE_LOCATION))))
                .toString()));
        final var release = client.getRelease(PURL).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo(String.format("/pypi/%s/%s/json", NAME, VERSION));
        assertThat(release.getSummary()).contains(SUMMARY);
        assertThat(release.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(release.getLicense()).contains(DECLARED_LICENSE);
        assertThat(release.getSourceUrl()).contains(URI.create(SOURCE_LOCATION));
    }

    @Test
    void acceptsEmptyMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("info", new JSONObject())
                .put("releases", new JSONObject())
                .toString()));

        final var release = client.getRelease(PURL).orElseThrow();

        assertThat(release).isInstanceOf(ReleaseDefinition.class);
    }


    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new PyPiClient(URI.create("http://localhost:1234"));

        assertThatThrownBy(() -> serverlessClient.getRelease(PURL))
                .isInstanceOf(PyPiException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.getRelease(PURL))
                .isInstanceOf(PyPiException.class)
                .hasMessageContaining("status 500");
    }
}
