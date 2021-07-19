/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.nuget.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.PackageMetadata;
import com.philips.research.bombase.core.nuget.NugetException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NugetClientTest {
    private static final int PORT = 1081;
    private static final PackageURL PURL = createPurl("pkg:nuget/name@version");
    private static final String TITLE = "Title";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String DESCRIPTION = "Description";
    private static final String AUTHORS = "Attribution1, Attribution2";
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String DOWNLOAD_LOCATION = "https://example.com/binary";
    private static final String SHA512 = "Sha512";
    private static final String CATALOG_ENTRY = "http://localhost:" + PORT +
            "/catalog0/data/timestamp/package.version.json";
    private static final String LICENSE_EXPRESSION = "MIT";
    private static final String LICENSE_URL = "https://example.com/mit-license";

    private final NugetClient client = new NugetClient(URI.create("http://localhost:" + PORT));
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
    void getsInitialMetaDataFromServer() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();

        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("title", TITLE)
                .put("description", DESCRIPTION)
                .put("projectUrl", HOMEPAGE)
                .put("authors", AUTHORS)
                .put("licenseExpression", LICENSE_EXPRESSION)
                .put("licenseUrl", LICENSE_URL)
                .put("repository", SOURCE_LOCATION)
                .put("packageHash", SHA512)
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/registration5-semver1/name/version.json");
        assertThat(definition.getTitle()).contains(TITLE);
        assertThat(definition.getDescription()).contains(DESCRIPTION);
        assertThat(definition.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(definition.getAuthors()).contains(List.of("Attribution1", "Attribution2"));
        assertThat(definition.getSourceLocation()).contains(SOURCE_LOCATION);
        assertThat(definition.getDeclaredLicense()).contains(LICENSE_URL);
        assertThat(definition.getDownloadLocation()).contains(URI.create(DOWNLOAD_LOCATION));
        assertThat(definition.getSha512()).contains(SHA512);
    }

    @Test
    void acceptsEmptyMetadataFromServer() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .toString()));

        final var release = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(release).isInstanceOf(PackageMetadata.class);
    }

    @Test
    void acceptsBareRepositoryURLs() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("repository", SOURCE_LOCATION)
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getSourceLocation()).contains(SOURCE_LOCATION);
    }

    @Test
    void withoutLicenseExpressionAndUrl() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();

        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("licenseExpression", null)
                .put("licenseUrl", null)
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getDeclaredLicense()).isEmpty();
    }

    private void enqueueNugetSpecXMLMock() {
        mockServer.enqueue(new MockResponse().setBody("<package>" +
                "<metadata>" +
                "<title>" + TITLE + "</title>" +
                "<description>" + DESCRIPTION + "</description>" +
                "<license type='expression'>" + LICENSE_EXPRESSION + "</license>" +
                "<licenseUrl>" + LICENSE_URL + "</licenseUrl>" +
                "<url>" + HOMEPAGE + "</url>" +
                String.format("<repository type='git' url='%s' />", SOURCE_LOCATION) +
                "</metadata>" +
                "</package>"));
    }

    @Test
    void withoutLicenseExpressionWithFilledUrl() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();

        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("licenseExpression", null)
                .put("licenseUrl", LICENSE_URL)
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getDeclaredLicense()).contains(LICENSE_URL);
    }

    @Test
    void withLicenseExpressionAndUrl() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();

        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("licenseExpression", LICENSE_EXPRESSION)
                .put("licenseUrl", LICENSE_URL)
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getDeclaredLicense()).contains(LICENSE_URL);
    }

    @Test
    void withoutCatalogEntry() throws Exception {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("catalogEntry", null)
                .toString()));

        final var definition = client.getPackageMetadata(PURL);

        assertThat(definition).isEmpty();
    }

    @Test
    void expandsListOfAuthors() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("authors", AUTHORS)
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getAuthors()).contains(List.of("Attribution1", "Attribution2"));
    }

    @Test
    void expandsListOfSingleAuthor() throws Exception {
        enqueueCatalogEntryMock();
        enqueueNugetSpecXMLMock();
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("authors", "Attribution1")
                .toString()));

        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        assertThat(definition.getAuthors()).contains(List.of("Attribution1"));
    }

    private void enqueueCatalogEntryMock() throws JSONException {
        mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                .put("catalogEntry", CATALOG_ENTRY)
                .put("packageContent", DOWNLOAD_LOCATION)
                .toString()));
    }

    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new NugetClient(URI.create("http://localhost:1234"));

        assertThatThrownBy(() -> serverlessClient.getPackageMetadata(PURL))
                .isInstanceOf(NugetException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.getPackageMetadata(PURL))
                .isInstanceOf(NugetException.class)
                .hasMessageContaining("status 500");
    }
}
