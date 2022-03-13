package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.PartEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigDetailsRepository extends JpaRepository<ConfigDetailsEntity, Long> {

    Optional<ConfigDetailsEntity> findByConfiguration(String configDetails);

    Optional<ConfigDetailsEntity> findByConfigurationAndPartEntity(String configDetails, PartEntity partEntity);

}
