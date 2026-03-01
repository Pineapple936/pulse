package com.pulse.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public abstract class CrudService<T, R extends JpaRepository<T, ID>, ID> {
    @Autowired
    protected R repository;

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public T findById(ID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity with id " + id + " not found")
        );
    }

    @Transactional
    public T update(ID id, T entity) {
        if(!repository.existsById(id))
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        return this.save(entity);
    }

    @Transactional
    public void delete(ID id) {
        repository.deleteById(id);
    }

    public abstract boolean hasUser(Long id, Long userId);
}
