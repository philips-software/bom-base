/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.clearlydefined.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.List;
import java.util.Optional;

class PackageMetadata {
    private @NullOr URI homepage;
    private @NullOr List<String> attribution;
    private @NullOr URI downloadLocation;
    private @NullOr URI sourceLocation;
    private @NullOr String declaredLicense;
    private @NullOr String detectedLicense;
    private @NullOr String sha1;
    private @NullOr String sha256;

    Optional<URI> getHomePage() {
        return Optional.ofNullable(homepage);
    }

    PackageMetadata setHomePage(URI url) {
        this.homepage = url;
        return this;
    }

    List<String> getAttribution() {
        return (attribution != null) ? attribution : List.of();
    }

    PackageMetadata setAttribution(List<String> attribution) {
        this.attribution = attribution;
        return this;
    }

    Optional<URI> getDownloadLocation() {
        return Optional.ofNullable(downloadLocation);
    }

    PackageMetadata setDownloadLocation(URI location) {
        this.downloadLocation = location;
        return this;
    }

    Optional<URI> getSourceLocation() {
        return Optional.ofNullable(sourceLocation);
    }

    PackageMetadata setSourceLocation(URI location) {
        this.sourceLocation = location;
        return this;
    }

    Optional<String> getDeclaredLicense() {
        return Optional.ofNullable(declaredLicense);
    }

    PackageMetadata setDeclaredLicense(String license) {
        this.declaredLicense = license;
        return this;
    }

    Optional<String> getDetectedLicense() {
        return Optional.ofNullable(detectedLicense);
    }

    PackageMetadata setDetectedLicense(String license) {
        this.detectedLicense = license;
        return this;
    }

    Optional<String> getSha1() {
        return Optional.ofNullable(sha1);
    }

    PackageMetadata setSha1(String hash) {
        this.sha1 = hash;
        return this;
    }
    Optional<String> getSha256() {
        return Optional.ofNullable(sha256);
    }

    PackageMetadata setSha256(String hash) {
        this.sha256 = hash;
       return this;
    }
}
