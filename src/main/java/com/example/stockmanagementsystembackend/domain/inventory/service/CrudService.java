package com.example.stockmanagementsystembackend.domain.inventory.service;

import java.util.List;

/**
 * Abstraction: inventory services expose one common CRUD contract while hiding
 * repository-specific persistence details from controllers.
 */
public interface CrudService<T, ID> {
    T create(T entity);

    /**
     * Standard persistence name used by CRUD services. The default keeps
     * existing inventory implementations backward compatible.
     */
    default T save(T entity) {
        return create(entity);
    }

    List<T> getAll();

    T getById(ID id);

    T update(ID id, T entity);

    void delete(ID id);
}
