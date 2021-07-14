/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.maven.MavenException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MavenClientTest {
    private static final int PORT = 1083;
    private static final PackageURL PURL = createPurl("pkg:maven/org.group/name@version");
    private static final String TITLE = "Title";
    private static final String HOMEPAGE = "https://example.com/home-page";
    private static final String DESCRIPTION = "Description";
    private static final String AUTHOR = "Attribution";
    private static final String SOURCE_LOCATION = "https://example.com/source";
    private static final String DOWNLOAD_LOCATION = "https://example.com/binary";
    private static final String SHA1 = "Sha1";
    private static final String DECLARED_LICENSE = "Declared";

    private final MavenClient client = new MavenClient("http://localhost:" + PORT);
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
        mockServer.enqueue(new MockResponse().setBody("<project>" +
                "<name>" + TITLE + "</name>" +
                "<description>" + DESCRIPTION + "</description>" +
                "<url>" + HOMEPAGE + "</url>" +
                "<organization>" +
                "    <name>" + AUTHOR + "</name>" +
                "</organization>" +
                "<licenses>" +
                "    <license>" +
                "        <name>" + DECLARED_LICENSE + "</name>" +
                "    </license>" +
                "</licenses>" +
                "<scm>" +
                "    <url>" + SOURCE_LOCATION + "</url>" +
                "</scm>" +
                "</project>"));
        final var definition = client.getPackageMetadata(PURL).orElseThrow();

        final var request = mockServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/org/group/name/version/name-version.pom");
        assertThat(definition.getTitle()).contains(TITLE);
        assertThat(definition.getDescription()).contains(DESCRIPTION);
        assertThat(definition.getHomepage()).contains(URI.create(HOMEPAGE));
        assertThat(definition.getAuthors()).contains(List.of(AUTHOR));
        assertThat(definition.getSourceLocation()).contains(SOURCE_LOCATION);
        assertThat(definition.getDeclaredLicense()).contains(DECLARED_LICENSE);
//        assertThat(definition.getDownloadLocation()).contains(URI.create(DOWNLOAD_LOCATION));
//        assertThat(definition.getSha1()).contains(SHA1);
    }

    @Test
    void handlesUnknownArtifact() {
        mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

        assertThat(client.getPackageMetadata(PURL)).isEmpty();
    }

    @Test
    void throws_serverNotReachable() {
        var serverlessClient = new MavenClient("http://localhost:1234");

        assertThatThrownBy(() -> serverlessClient.getPackageMetadata(PURL))
                .isInstanceOf(MavenException.class)
                .hasMessageContaining("not reachable");
    }

    @Test
    void throws_unexpectedResponseFromServer() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.getPackageMetadata(PURL))
                .isInstanceOf(MavenException.class)
                .hasMessageContaining("status 500");
    }
}
