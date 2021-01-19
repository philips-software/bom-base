package com.philips.research.metabase.meta;

import java.net.URI;

public class UnknownPackageException extends MetaException {
    public UnknownPackageException(URI pkg) {
        super("Package '" + pkg + "' is unknown");
    }
}
