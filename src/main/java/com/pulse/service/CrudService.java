package com.pulse.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public abstract class CrudService<T, R extends JpaRepository<T, ID>, ID> {
    @Autowired
    protected R repository;

    @Transactional
    public T save(T entity) {
        log.debug("Persist entity type={}", entity.getClass().getSimpleName());
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        log.debug("Find all entities");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public T findById(ID id) {
        log.debug("Find entity by id={}", id);
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity with id " + id + " not found")
        );
    }

    @Transactional
    public T update(ID id, T entity) {
        log.debug("Update entity id={} type={}", id, entity.getClass().getSimpleName());
        if(!repository.existsById(id)) {
            log.warn("Entity not found for update id={}", id);
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
        return this.save(entity);
    }

    @Transactional
    public void delete(ID id) {
        log.debug("Delete entity id={}", id);
        repository.deleteById(id);
    }

    public abstract boolean hasUser(Long id, Long userId);
}
