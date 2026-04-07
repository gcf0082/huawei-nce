package com.huawei.nce.business.repository;

import com.huawei.nce.business.model.ConfigTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigTemplateRepository extends JpaRepository<ConfigTemplate, Long> {
    Optional<ConfigTemplate> findByName(String name);
    boolean existsByName(String name);
}
