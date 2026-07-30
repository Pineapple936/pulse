package com.pulse.repository.interfaces;

import com.pulse.service.pagination.Page;

import java.util.List;

public interface FindableByParent<T> extends EntityRepository<T> {
    List<T> findByParent(Long parentId, Page page);
}
