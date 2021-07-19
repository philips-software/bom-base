/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.maven.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.maven.MavenException;
import com.philips.research.bombase.core.meta.PackageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Component
public class MavenClient {
    private static final Logger LOG = LoggerFactory.getLogger(MavenClient.class);
    private static final ObjectMapper MAPPER = new XmlMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE);

    private final HttpClient client;
    private final URI baseUri;

    @Autowired
    MavenClient() {
        this("https://repo1.maven.org/maven2/");
    }

    MavenClient(String baseUri) {
        client = HttpClient.newHttpClient();
        this.baseUri = URI.create(baseUri + '/');
    }

    Optional<PackageMetadata> getPackageMetadata(PackageURL purl) {
        try {
            final HttpRequest request = pomRequest(purl);
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                case 404:
                    return Optional.empty();
                case 200:
                    return Optional.of(MAPPER.readValue(response.body(), PomXml.class));
                default:
                    LOG.info("Query={}", request.uri());
                    throw new MavenException("Maven server responded with status " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new MavenException("Maven server is not reachable", e);
        }
    }

    private HttpRequest pomRequest(PackageURL purl) {
        final var path = purl.getNamespace().replaceAll("\\.", "/");
        final var name = purl.getName();
        final var version = purl.getVersion();
        final var pom = String.format("%s-%s.pom", name, version);
        final var uri = baseUri.resolve(path + '/').resolve(name + '/').resolve(version + '/').resolve(pom);
        return HttpRequest.newBuilder(uri).GET().build();
    }
}
