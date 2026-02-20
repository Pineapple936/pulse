package com.example.demo.service;

import com.example.demo.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class CrudService<T, R extends JpaRepository<T, ID>, ID> {
    protected R repository;

    @Autowired
    public void setRepository(R repository) {
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

    public abstract boolean hasUser(T entry, User user);
}
