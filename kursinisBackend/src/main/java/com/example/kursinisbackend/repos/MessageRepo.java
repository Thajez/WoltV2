package com.example.kursinisbackend.repos;

import com.example.kursinisbackend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Integer> {
}
