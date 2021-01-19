package com.philips.research.metabase.maven.domain;

import com.philips.research.metabase.maven.MavenService;
import com.philips.research.metabase.meta.Field;
import com.philips.research.metabase.meta.MetaService;

import java.net.URI;
import java.util.Optional;

public class MavenInteractor implements MavenService {
    private final MetaService meta;

    public MavenInteractor(MetaService meta) {
        this.meta = meta;
    }

    @Override
    public void init() {
        meta.addListener((purl, fields, values) -> {
            if (!fields.contains(Field.TYPE) || !values.get(Field.TYPE).equals("maven")) {
                return Optional.empty();
            }

            return Optional.of(() -> {
                final var value = meta.value(purl);
                final var name = (String) value.get(Field.NAME);
                final var pos = name.indexOf('/');
                final var group = (pos >= 0) ? name.substring(0, pos) : "";
                final var artifact = (pos >= 0) ? name.substring(pos + 1) : name;
                final var version = (String) value.get(Field.VERSION);
                this.updatePackage(purl, group, artifact, version);
            });
        });
    }

    @Override
    public void updatePackage(URI purl, String group, String artifact, String version) {

    }
}
