package com.ecosense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecosense.entity.Site;

@Repository
public interface SiteJpaRepo extends JpaRepository<Site, Integer> {

}
