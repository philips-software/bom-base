package com.philips.research.metabase.activity;

import java.net.URI;

public class UnknownPackageException extends MetaException {
    public UnknownPackageException(URI pkg) {
        super("Package '" + pkg + "' does not exist");
    }
}
