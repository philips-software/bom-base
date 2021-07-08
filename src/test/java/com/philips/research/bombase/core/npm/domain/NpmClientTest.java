/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.npm.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.npm.NpmException;
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

class NpmClientTest {
    private static final int PORT = 1081;
    private static final String TYPE = "type";
    private static final String NAMESPACE = "Namespace";
    private static final String NAME = "Name";
    private static final String VERSION = "Release";
    private static final PackageURL PURL = createPurl(String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION));
    private static final String DESCRIPTION = "Description";
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String DOWNLOAD_LOCATION = "https://example.com/binary";
    private static final String SHA1 = "Sha1";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String DECLARED_LICENSE = "Declared";
    private static final String ATTRIBUTION = "Attribution";

    private final NpmClient client = new NpmClient(URI.create("http://localhost:" + PORT));
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

        assertThat(client.getPackage(PURL)).isEmpty();
    }

    @Test
    void getsMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("name", NAME)
                .put("description", DESCRIPTION)
                .put("home_page", HOMEPAGE)
                .put("author", new JSONObject()
                        .put("name", ATTRIBUTION))
                .put("license", DECLARED_LICENSE)
                .put("repository", new JSONObject()
                        .put("url", SOURCE_LOCATION))
                .put("dist", new JSONObject()
                        .put("tarball", DOWNLOAD_LOCATION)
                        .put("shasum", SHA1))
                .toString()));
        final var definition = client.getPackage(PURL).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo(String.format("/%s/%s", NAME, VERSION));
        assertThat(definition.getName()).contains(NAME);
        assertThat(definition.getDescription()).contains(DESCRIPTION);
        assertThat(definition.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(definition.getLicense()).contains(DECLARED_LICENSE);
        assertThat(definition.getSourceUrl()).contains(SOURCE_LOCATION);
        assertThat(definition.getDownloadUrl()).contains(URI.create(DOWNLOAD_LOCATION));
        assertThat(definition.getSha()).contains(SHA1);
        assertThat(definition.getAuthors()).contains(List.of(ATTRIBUTION));
    }

    @Test
    void acceptsEmptyMetadataFromServer() {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .toString()));

        final var release = client.getPackage(PURL).orElseThrow();

        assertThat(release).isInstanceOf(PackageDefinition.class);
    }

    @Test
    void expandsListOfLicenses() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("license", new JSONArray()
                        .put("license1")
                        .put("license2"))
                .toString()));

        final var definition = client.getPackage(PURL).orElseThrow();

        assertThat(definition.getLicense()).contains("license1 AND license2");
    }

    @Test
    void handlesLicenseObjectFormat() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("license", new JSONObject()
                        .put("type", DECLARED_LICENSE))
                .toString()));

        final var definition = client.getPackage(PURL).orElseThrow();

        assertThat(definition.getLicense()).contains(DECLARED_LICENSE);
    }

    @Test
    void expandsListOfAuthors() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("author", new JSONArray()
                        .put(new JSONObject()
                                .put("name", "name1"))
                        .put(new JSONObject()
                                .put("name", "name2")))
                .toString()));

        final var definition = client.getPackage(PURL).orElseThrow();

        assertThat(definition.getAuthors()).contains(List.of("name1", "name2"));
    }

    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new NpmClient(URI.create("http://localhost:1234"));

        assertThatThrownBy(() -> serverlessClient.getPackage(PURL))
                .isInstanceOf(NpmException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.getPackage(PURL))
                .isInstanceOf(NpmException.class)
                .hasMessageContaining("status 500");
    }
}
