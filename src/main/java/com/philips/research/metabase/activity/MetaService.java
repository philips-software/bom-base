package com.philips.research.metabase.activity;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public interface MetaService {

    /**
     * Registers an observer for value changes.
     *
     * @param listener observer
     */
    void addListener(PackageListener listener);

    /**
     * Updates fields of package and processes notifications.
     *
     * @param purl   package URL
     * @param values new value per field
     */
    void update(URI purl, Map<Field, Object> values);

    /**
     * Reads current field values of a package.
     *
     * @param purl package URL
     * @return value per field
     * @throws UnknownPackageException if the package does not exist
     */
    Map<Field, Object> value(URI purl);

    /**
     * Callbacks to optionally create an asynchronous task.
     */
    interface PackageListener {
        Optional<Runnable> onUpdated(URI pkg, Field field, Object value);
    }
}
