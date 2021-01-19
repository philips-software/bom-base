package com.philips.research.metabase.maven;

import java.net.URI;

public interface MavenService {
    void init();

    void updatePackage(URI purl, String group, String name, String version);
}
