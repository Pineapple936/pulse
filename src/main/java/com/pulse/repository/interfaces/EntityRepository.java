package com.pulse.repository.interfaces;

import java.util.Optional;

public interface EntityRepository<T> {
    T save(T entity);
    Optional<T> findById(Long id);
    void deleteById(Long id);
}
