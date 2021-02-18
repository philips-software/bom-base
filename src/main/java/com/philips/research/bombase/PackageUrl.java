/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase;

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public final class PackageUrl {
    private final String type;
    private final @NullOr String namespace;
    private final String name;
    private final String version;

    public PackageUrl(String type, @NullOr String namespace, String name, String version) {
        this.type = type;
        this.namespace = namespace;
        this.name = name;
        this.version = version;
    }

    public PackageUrl(String purl) {
        this(URI.create(purl));
    }

    public PackageUrl(URI purl) {
        validateScheme(purl);
        final var nameParts = namePartsOf(purl);
        type = decode(nameParts[0]);
        final var hasNamespace = nameParts.length > 2;
        namespace = hasNamespace ? decode(nameParts[1]) : null;
        final var versionParts = nameParts[hasNamespace ? 2 : 1].split("@");
        if (versionParts.length < 2) {
            throw new IllegalArgumentException("Package URL must include a version");
        }
        name = decode(versionParts[0]);
        version = decode(versionParts[1]);
    }

    private void validateScheme(URI purl) {
        final var scheme = purl.getScheme();
        if (scheme != null && !scheme.equals("pkg")) {
            throw new IllegalArgumentException("Package URL scheme must be 'pkg'");
        }
    }

    private String[] namePartsOf(URI purl) {
        final var path = purl.getRawSchemeSpecificPart();
        final var base = path.split("[#?]")[0];
        final var nameParts = base.split("/");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Package URL must contain a type and name");
        }
        if (nameParts.length > 3) {
            throw new IllegalArgumentException("Package URL can contain maximum two name parts");
        }
        return nameParts;
    }

    private String decode(String string) {
        return URLDecoder.decode(string, StandardCharsets.UTF_8);
    }

    public String getType() {
        return type;
    }

    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public URI toUri() {
        final var ns = (namespace != null) ? (encoded(namespace) + '/') : "";
        return URI.create("pkg:" + type + '/' + ns + encoded(name) + '@' + encoded(version));
    }

    private String encoded(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageUrl)) return false;
        PackageUrl that = (PackageUrl) o;
        return type.equals(that.type)
                && Objects.equals(namespace, that.namespace)
                && name.equals(that.name)
                && version.equals(that.version);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(type, namespace, name, version);
    }

    @Override
    public String toString() {
        return toUri().toString();
    }
}
