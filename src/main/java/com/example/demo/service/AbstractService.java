package com.example.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class AbstractService<T, ID> {
    private final JpaRepository<T, ID> repository;

    public AbstractService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T findById(ID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity with id " + id + " not found")
        );
    }

    public T update(ID id, T entity) {
        if(!repository.existsById(id))
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        return this.save(entity);
    }

    public void delete(ID id) {
        repository.deleteById(id);
    }
}
