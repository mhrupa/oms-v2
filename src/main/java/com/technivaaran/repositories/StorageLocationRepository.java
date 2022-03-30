package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.StorageLocationEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageLocationRepository extends JpaRepository<StorageLocationEntity, Integer> {

    Optional<StorageLocationEntity> findByLocationName(String boxName);

}
