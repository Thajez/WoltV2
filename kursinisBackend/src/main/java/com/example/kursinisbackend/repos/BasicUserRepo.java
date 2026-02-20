package com.example.kursinisbackend.repos;

import com.example.kursinisbackend.model.BasicUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicUserRepo extends JpaRepository<BasicUser, Integer> {
}
