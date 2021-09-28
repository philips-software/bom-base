/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.clearlydefined.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.clearlydefined.ClearlyDefinedException;
import com.philips.research.bombase.core.meta.PackageMetadata;
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

class ClearlyDefinedClientTest {
    private static final int PORT = 1080;
    private static final String TYPE = "type";
    private static final String NAMESPACE = "Namespace";
    private static final String NAME = "Name";
    private static final String VERSION = "Revision";
    private static final PackageURL PURL = createPurl(String.format("pkg:%s/%s/%s@%s", TYPE, NAMESPACE, NAME, VERSION));
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String DOWNLOAD_LOCATION = "https://example.com/download";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String SHA1 = "Sha1";
    private static final String SHA256 = "Sha256";
    private static final String DECLARED_LICENSE = "Declared";
    private static final String DETECTED_LICENSE = "Detected";
    private static final String ATTRIBUTION = "Attribution";

    private final ClearlyDefinedClient client = new ClearlyDefinedClient(URI.create("http://localhost:" + PORT));
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
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("scores", new JSONObject()
                        .put("effective", 0)).toString()));

        assertThat(client.getPackageMetadata(PURL)).isEmpty();
    }

    @Test
    void getsMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("described", new JSONObject()
                        .put("sourceLocation", new JSONObject()
                                .put("name", NAME)
                                .put("url", SOURCE_LOCATION))
                        .put("urls", new JSONObject()
                                .put("download", DOWNLOAD_LOCATION))
                        .put("projectWebsite", HOMEPAGE)
                        .put("hashes", new JSONObject()
                                .put("sha1", SHA1)
                                .put("sha256", SHA256))
                        .put("score", new JSONObject()
                                .put("total", 100)))
                .put("licensed", new JSONObject()
                        .put("declared", DECLARED_LICENSE)
                        .put("facets", new JSONObject()
                                .put("core", new JSONObject()
                                        .put("attribution", new JSONObject()
                                                .put("parties", new JSONArray()
                                                        .put(ATTRIBUTION)))
                                        .put("discovered", new JSONObject()
                                                .put("expressions", new JSONArray()
                                                        .put(DETECTED_LICENSE)))))
                        .put("score", new JSONObject()
                                .put("total", 100)))
                .put("scores", new JSONObject()
                        .put("effective", 100)).toString()));

        final var metadata = client.getPackageMetadata(PURL).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo(String.format("/definitions/%s/%s/%s/%s/%s", TYPE, TYPE, NAMESPACE, NAME, VERSION));
        assertThat(metadata.getTitle()).contains(NAME);
        assertThat(metadata.getSourceLocation()).contains(SOURCE_LOCATION);
        assertThat(metadata.getDownloadLocation()).contains(URI.create(DOWNLOAD_LOCATION));
        assertThat(metadata.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(metadata.getSha1()).contains(SHA1);
        assertThat(metadata.getSha256()).contains(SHA256);
        assertThat(metadata.getDeclaredLicense()).contains(DECLARED_LICENSE);
        assertThat(metadata.getDetectedLicenses()).contains(List.of(DETECTED_LICENSE));
        assertThat(metadata.getAuthors()).contains(List.of(ATTRIBUTION));
    }

    @Test
    void acceptsEmptyMetadataFromServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("described", new JSONObject())
                .put("licensed", new JSONObject())
                .put("scores", new JSONObject()
                        .put("effective", 100)).toString()));

        final var metadata = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(metadata).isInstanceOf(PackageMetadata.class);
    }

    @Test
    void ignoresNoAssertionLicense() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("licensed", new JSONObject()
                        .put("declared", "NOASSERTION"))
                .put("scores", new JSONObject()
                        .put("effective", 100))
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getDeclaredLicense()).isEmpty();
    }

    @Test
    void ignoresOtherLicense() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("licensed", new JSONObject()
                        .put("declared", "OTHER"))
                .put("scores", new JSONObject()
                        .put("effective", 100))
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getDeclaredLicense()).isEmpty();
    }

    @Test
    void mapsPurlTypeToClearlyDefinedProvider() throws Exception {
        mockServer.enqueue(new MockResponse().setBody("{}"));
        client.getPackageMetadata(new PackageURL("cargo", NAMESPACE, NAME, VERSION, null, null));

        final var request = mockServer.takeRequest();
        assertThat(request.getPath()).contains("/crate/cratesio/");
    }

    @Test
    void escapesEmptyNamespaceToServer() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("described", new JSONObject())
                .put("licensed", new JSONObject())
                .put("scores", new JSONObject()
                        .put("effective", 100)).toString()));

        client.getPackageMetadata(new PackageURL(TYPE, null, NAME, VERSION, null, null)).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getPath()).contains(TYPE + "/-/" + NAME);
    }

    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new ClearlyDefinedClient(URI.create("http://localhost:1234"));

        assertThatThrownBy(() -> serverlessClient.getPackageMetadata(PURL))
                .isInstanceOf(ClearlyDefinedException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.getPackageMetadata(PURL))
                .isInstanceOf(ClearlyDefinedException.class)
                .hasMessageContaining("status 404");
    }
}
