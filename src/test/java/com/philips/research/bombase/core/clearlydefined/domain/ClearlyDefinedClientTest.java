/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.philips.research.bombase.core.clearlydefined.ClearlyDefinedException;
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

class ClearlyDefinedClientTest {
    private static final int PORT = 1080;
    private static final String TYPE = "Type";
    private static final String PROVIDER = "Provider";
    private static final String NAMESPACE = "Namespace";
    private static final String NAME = "Name";
    private static final String REVISION = "Revision";
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String DOWNLOAD_LOCATION = "https://example.com/download";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String SHA1 = "Sha1";
    private static final String SHA256 = "Sha256";
    private static final String DECLARED_LICENSE = "Declared";
    private static final String DETECTED_LICENSE = "Declared";
    private static final String ATTRIBUTION = "Attribution";

    private final ClearlyDefinedClient client = new ClearlyDefinedClient(URI.create("http://localhost:" + PORT));
    private final MockWebServer mockServer = new MockWebServer();

    @BeforeEach
    void setUp() throws IOException {
        mockServer.start(PORT);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void getsMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("described", new JSONObject()
                        .put("sourceLocation", new JSONObject()
                                .put("url", SOURCE_LOCATION))
                        .put("urls", new JSONObject()
                                .put("download", DOWNLOAD_LOCATION))
                        .put("projectWebsite", HOMEPAGE)
                        .put("hashes", new JSONObject()
                                .put("sha1", SHA1)
                                .put("sha256", SHA256)))
                .put("licensed", new JSONObject()
                        .put("declared", DECLARED_LICENSE)
                        .put("facets", new JSONObject()
                                .put("core", new JSONObject()
                                        .put("attribution", new JSONObject()
                                                .put("parties", new JSONArray()
                                                        .put(ATTRIBUTION)))
                                        .put("discovered", new JSONObject()
                                                .put("expressions", new JSONArray()
                                                        .put(DETECTED_LICENSE)))))).toString()));

        final var definition = client.getPackageDefinition(TYPE, PROVIDER, NAMESPACE, NAME, REVISION).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo(String.format("/definitions/%s/%s/%s/%s/%s", TYPE, PROVIDER, NAMESPACE, NAME, REVISION));
        assertThat(definition.getSourceLocation()).contains(URI.create(SOURCE_LOCATION));
        assertThat(definition.getDownloadLocation()).contains(URI.create(DOWNLOAD_LOCATION));
        assertThat(definition.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(definition.getSha1()).contains(SHA1);
        assertThat(definition.getSha256()).contains(SHA256);
        assertThat(definition.getDeclaredLicense()).contains(DECLARED_LICENSE);
        assertThat(definition.getDetectedLicenses()).contains(DETECTED_LICENSE);
        assertThat(definition.getAuthors()).contains(ATTRIBUTION);
    }

    @Test
    void acceptsEmptyMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("described", new JSONObject())
                .put("licensed", new JSONObject()).toString()));

        final var metadata = client.getPackageDefinition(TYPE, PROVIDER, NAMESPACE, NAME, REVISION).orElseThrow();

        assertThat(metadata).isInstanceOf(PackageDefinition.class);
    }

    @Test
    void escapesEmptyNamespaceToServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("described", new JSONObject())
                .put("licensed", new JSONObject()).toString()));

        final var metadata = client.getPackageDefinition(TYPE, PROVIDER, "", NAME, REVISION).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getPath()).contains(PROVIDER + "/-/" + NAME);
    }

    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new ClearlyDefinedClient(URI.create("http://localhost:1234"));

        assertThatThrownBy(() -> serverlessClient.getPackageDefinition(TYPE, PROVIDER, NAMESPACE, NAME, REVISION))
                .isInstanceOf(ClearlyDefinedException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.getPackageDefinition(TYPE, PROVIDER, NAMESPACE, NAME, REVISION))
                .isInstanceOf(ClearlyDefinedException.class)
                .hasMessageContaining("status 404");
    }
}
