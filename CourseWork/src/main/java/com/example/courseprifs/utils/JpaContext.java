package com.example.courseprifs.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Getter;

public class JpaContext {
    @Getter
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("CoursePrifsDB");

    private JpaContext() {}

    public static void closeEmf() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}