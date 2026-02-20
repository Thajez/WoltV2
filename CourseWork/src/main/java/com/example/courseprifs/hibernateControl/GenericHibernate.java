package com.example.courseprifs.hibernateControl;

import com.example.courseprifs.utils.FxUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaQuery;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {
    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public <T> void create(T entity) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(entity); //INSERT
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "Error during CREATE operation", e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public <T> void update(T entity) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(entity); //UPDATE
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "Error during UPDATE operation", e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public <T> T getEntityById(Class<T> entityClass, int id) {
        T entity = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entity = entityManager.find(entityClass, id);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "Error during FETCH BY ID operation", e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return entity;
    }

    public <T> void delete(Class<T> entityClass, int id) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            T entity = entityManager.find(entityClass, id);

            if (entity == null) {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Delete failed", null, "Entity not found");
                return;
            }

            entityManager.remove(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "Error during DELETE operation", e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public <T> List<T> getAllRecords(Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaQuery<T> query = entityManager
                    .getCriteriaBuilder()
                    .createQuery(entityClass);

            query.select(query.from(entityClass));
            list = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "Error fetching all records", e);
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return list;
    }
}
