/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.core.meta.registry;

import com.github.packageurl.PackageURL;
import com.philips.research.bombase.core.meta.MetaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class MetaRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(MetaRegistry.class);

    private final MetaStore store;
    private final QueuedTaskRunner runner;
    private final Set<PackageListener> listeners = new HashSet<>();

    public MetaRegistry(MetaStore store, QueuedTaskRunner runner) {
        this.store = store;
        this.runner = runner;
    }

    private static String nameFor(Object object) {
        return object.getClass().getSimpleName().replace("Listener", "");
    }

    /**
     * Registers an observer for metadata value changes.
     *
     * @param listener observer
     */
    public void addListener(PackageListener listener) {
        LOG.info("Registered {} listener", nameFor(listener));
        listeners.add(listener);
    }

    public Optional<Map<Field, AttributeValue<?>>> getAttributeValues(PackageURL purl) {
        return store.findPackage(purl)
                .map(pkg -> pkg.getAttributes()
                        .collect(Collectors.toMap(Attribute::getField, a -> a)));
    }

    public void edit(PackageURL purl, Consumer<PackageAttributeEditor> consumer) {
        final var pkg = getOrCreatePackage(purl);
        final var editor = new PackageAttributeEditor(pkg);
        consumer.accept(editor);
        cascadeListeners(editor);
    }

    private Package getOrCreatePackage(PackageURL purl) {
        return store.findPackage(purl).orElseGet(() -> createPackage(purl));
    }

    private Package createPackage(PackageURL purl) {
        final var pkg = store.createPackage(purl);
        LOG.info("Created new package {}", purl);
        notifyListeners(purl, Set.of(), Map.of());
        return pkg;
    }

    private void cascadeListeners(PackageAttributeEditor editor) {
        if (editor.isModified()) {
            final var modifiedFields = editor.getModifiedFields();
            LOG.info("Updated {}: {}", editor.getPurl(), modifiedFields);
            notifyListeners(editor.getPurl(), modifiedFields, editor.getValues());
        } else {
            LOG.info("No update of {}", editor.getPurl());
        }
    }

    private void notifyListeners(PackageURL purl, Set<Field> modifiedFields, Map<Field, Object> values) {
        listeners.forEach(l -> l.onUpdated(purl, modifiedFields, values)
                .ifPresent(task -> {
                    LOG.info("Scheduled {} task for {}", nameFor(l), purl);
                    runner.execute(purl, task, this::cascadeListeners);
                }));
    }

    /**
     * Callbacks to optionally create an asynchronous task.
     */
    public interface PackageListener {
        /**
         * Notifies given fields were updated.
         *
         * @param purl    package id
         * @param updated modified fields
         * @param values  current package metadata
         * @return (optional) operation to queue for execution
         */
        Optional<Consumer<PackageAttributeEditor>> onUpdated(PackageURL purl, Set<Field> updated, Map<Field, Object> values);
    }
}
