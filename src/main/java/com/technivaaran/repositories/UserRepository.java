package com.technivaaran.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findUserByEmailAndPassword(String email, String password);

    public Optional<User> findByUserNameAndPassword(String userName, String password);
}
