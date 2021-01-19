package com.philips.research.metabase.activity;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface MetaService {

    /**
     * Registers an observer for value changes in packages of one type.
     *
     * @param type     name of the type
     * @param listener observer
     */
    void addTypeListener(Field type, FieldListener listener);

    /**
     * Registers an observer for value changes in one field of any package.
     *
     * @param field    name of the field
     * @param listener observer
     */
    void addFieldListener(Field field, FieldListener listener);

    /**
     * Creates or updates the default value of a field.
     *
     * @param pkg   package to update
     * @param field name of the field
     * @param value new value
     */
    <T> void storeFieldValue(URI pkg, Field field, T value);

    /**
     * Marks a field as possibly incorrect
     *
     * @param pkg   package of the field
     * @param field name of the contested field
     * @param value expected value
     */
    void contestField(URI pkg, Field field, Object value);

    /**
     * Corrects value of a (contested) field
     *
     * @param pkg   package of the field
     * @param field name of the field to override
     * @param value correction value
     */
    void overrideField(URI pkg, Field field, Object value);

    /**
     * Removes a field from a package
     *
     * @param pkg   package of the field
     * @param field name of the field
     */
    void clearField(URI pkg, Field field);

    /**
     * @param pkg package identifier
     * @return map all fields with their value for the specified package
     */
    Map<String, Object> value(URI pkg);

    /**
     * @param field name of the field
     * @param limit maximum number of resulting packages
     * @return Packages where the indicated field value is contested
     */
    List<URI> contested(Field field, int limit);

    /**
     * Callbacks to optionally create an asynchronous task.
     */
    interface FieldListener {
        Runnable onStore(URI pkg, Field field, Object value);

        Runnable onContest(URI pkg, Field field, Object value);

        Runnable onOverride(URI pkg, Field field, Object value);

        Runnable onDelete(URI pkg, Field field, Object value);
    }
}
