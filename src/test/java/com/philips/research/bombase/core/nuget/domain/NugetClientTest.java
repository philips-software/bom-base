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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NugetClientTest {
    private static final int PORT = 1084;
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

    @Nested
    class WithoutMetaData {

        @Test
        void noMetaData_undefinedPackage() {
            mockServer.enqueue(new MockResponse().setResponseCode(404));

            assertThat(client.getPackageMetadata(PURL)).isEmpty();
        }

        @Test
        void noMetaData_noCatalogEntry() throws Exception {
            mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                    .put("catalogEntry", null)
                    .toString()));

            final var definition = client.getPackageMetadata(PURL);

            assertThat(definition).isEmpty();
        }

        @Test
        void throws_unexpectedResponseFromServer() {
            mockServer.enqueue(new MockResponse().setResponseCode(500));

            assertThatThrownBy(() -> client.getPackageMetadata(PURL))
                    .isInstanceOf(NugetException.class)
                    .hasMessageContaining("status 500");
        }

        @Test
        void throws_serverNotReachable() {
            var serverlessClient = new NugetClient(URI.create("http://localhost:1234"));

            assertThatThrownBy(() -> serverlessClient.getPackageMetadata(PURL))
                    .isInstanceOf(NugetException.class)
                    .hasMessageContaining("not reachable");
        }
    }

    @Nested
    class WithMetaData {

        @BeforeEach
        void setUp() throws JSONException {
            enqueueCatalogEntryMock();
            enqueueNugetSpecXMLMock();
        }

        @Test
        void verifyCorrectRequests() throws Exception {
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

            client.getPackageMetadata(PURL).orElseThrow();

            final var registrationRequest = mockServer.takeRequest();
            final var containerRequest = mockServer.takeRequest();
            final var catalogRequest = mockServer.takeRequest();

            assertThat(registrationRequest.getMethod()).isEqualTo("GET");
            assertThat(registrationRequest.getPath()).isEqualTo("/registration5-semver1/name/version.json");

            assertThat(containerRequest.getMethod()).isEqualTo("GET");
            assertThat(containerRequest.getPath()).isEqualTo("/flatcontainer%2Fname%2Fversion%2Fname.nuspec");

            assertThat(catalogRequest.getMethod()).isEqualTo("GET");
            assertThat(catalogRequest.getPath()).isEqualTo("/%2Fcatalog0%2Fdata%2Ftimestamp%2Fpackage.version.json");
        }

        @Test
        void getsInitialMetaDataFromServer() throws Exception {
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
        void preferLicenseUrl_withLicenseExpressionAndUrl() throws Exception {
            mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                    .put("licenseExpression", LICENSE_EXPRESSION)
                    .put("licenseUrl", LICENSE_URL)
                    .toString()));

            final var definition = client.getPackageMetadata(PURL).orElseThrow();

            assertThat(definition.getDeclaredLicense()).contains(LICENSE_URL);
        }

        @Test
        void returnEmptyPackageMetaData_emptyResponse() {
            mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                    .toString()));

            final var release = client.getPackageMetadata(PURL).orElseThrow();

            assertThat(release).isInstanceOf(PackageMetadata.class);
        }

        private void enqueueCatalogEntryMock() throws JSONException {
            mockServer.enqueue(new MockResponse().setBody(new JSONObject()
                    .put("catalogEntry", CATALOG_ENTRY)
                    .put("packageContent", DOWNLOAD_LOCATION)
                    .toString()));
        }

        private void enqueueNugetSpecXMLMock() {
            mockServer.enqueue(new MockResponse().setBody("<package>" +
                    "<metadata>" +
                    String.format("<repository type='git' url='%s' />", SOURCE_LOCATION) +
                    "</metadata>" +
                    "</package>"));
        }
    }
}
