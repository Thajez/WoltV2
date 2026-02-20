package com.example.kursinisbackend.repos;

import com.example.kursinisbackend.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepo extends JpaRepository<Driver, Integer> {
}
