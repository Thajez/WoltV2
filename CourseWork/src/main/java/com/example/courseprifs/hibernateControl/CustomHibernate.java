package com.example.courseprifs.hibernateControl;

import com.example.courseprifs.utils.FxUtils;
import com.example.courseprifs.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.courseprifs.utils.FxUtils.generateAlert;

public class CustomHibernate extends GenericHibernate {

    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String login, String plainPassword) {
        User user;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.equal(root.get("login"), login));

            user = entityManager.createQuery(query).getSingleResult();
            if (!FxUtils.checkPassword(plainPassword, user.getPassword())
                    || user.getUserType().equals(UserType.BASIC) || user.getUserType().equals(UserType.DRIVER)) user = null;
        } catch (Exception e) {
            user = null;
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return user;
    }

    public List<User> getFilteredUsers(Integer id, UserType userType, String name, String surname, String phone) {
        List<User> users = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (userType != null) {
                predicates.add(cb.equal(root.get("userType"), userType));
            }

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (surname != null && !surname.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%"));
            }

            if (phone != null && !phone.trim().isEmpty()) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + phone + "%"));
            }

            if (!predicates.isEmpty()) {
                query.select(root).where(cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
            } else {
                query.select(root);
            }

            users = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error filtering users",null, "Failed to filter users:");
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return users;
    }

    public List<FoodOrder> getRestaurantOrders(Restaurant restaurant) {
        List<FoodOrder> orders = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FoodOrder> query = cb.createQuery(FoodOrder.class);
            Root<FoodOrder> root = query.from(FoodOrder.class);

            query.select(root).where(cb.equal(root.get("restaurant"), restaurant));

            orders = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error retrieving orders",null, "Failed to load restaurant orders");
        } finally{
            if (entityManager != null) entityManager.close();
        }
        return orders;
    }

    public List<Cuisine> getRestaurantCuisine(Restaurant restaurant) {
        List<Cuisine> menu = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Cuisine> query = cb.createQuery(Cuisine.class);
            Root<Cuisine> root = query.from(Cuisine.class);

            query.select(root).where(cb.equal(root.get("restaurant"), restaurant));
            menu = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error retrieving cuisine",null, "Failed to load restaurant menu");
        } finally{
            if (entityManager != null) entityManager.close();
        }
        return menu;
    }

    public List<FoodOrder> getFilteredRestaurantOrders(Integer id, LocalDate dateFrom, LocalDate dateTo, OrderStatus status, BasicUser client, Driver driver, Restaurant restaurant) {
        List<FoodOrder> orders = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FoodOrder> query = cb.createQuery(FoodOrder.class);
            Root<FoodOrder> root = query.from(FoodOrder.class);

            List<Predicate> predicates = new ArrayList<>();

            if (id != null) predicates.add(cb.equal(root.get("id"), id));
            if (dateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dateUpdated"), dateFrom));
            if (dateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("dateUpdated"), dateTo));
            if (status != null) predicates.add(cb.equal(root.get("orderStatus"), status));
            if (client != null) predicates.add(cb.equal(root.get("buyer"), client));
            if (driver != null) predicates.add(cb.equal(root.get("driver"), driver));
            if (restaurant != null) predicates.add(cb.equal(root.get("restaurant"), restaurant));

            query.where(cb.and(predicates.toArray(new Predicate[0])));
            query.orderBy(cb.desc(root.get("dateCreated")));
            orders = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error retrieving orders",null, "Failed to load restaurant orders");
        } finally{
            if (entityManager != null) entityManager.close();
        }
        return orders;
    }

    public List<FoodOrderItem> getOrderItems(FoodOrder order) {
        List<FoodOrderItem> items = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FoodOrderItem> query = cb.createQuery(FoodOrderItem.class);
            Root<FoodOrderItem> root = query.from(FoodOrderItem.class);

            query.select(root).where(cb.equal(root.get("order"), order));

            items = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error retrieving order items", null, "Failed to load order items");
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return items;
    }

    public Chat getOrCreateChatForOrder(FoodOrder order) {
        entityManager = entityManagerFactory.createEntityManager();
        Chat chat = null;
        try {
            entityManager.getTransaction().begin();

            FoodOrder managedOrder = entityManager.find(FoodOrder.class, order.getId());

            chat = managedOrder.getChat();

            if (chat == null) {
                chat = findChatByOrder(managedOrder);

                if (chat == null) {
                    chat = createNewChat(managedOrder);
                }
            }
            if (chat.getMessages() != null) {
                chat.getMessages().size();
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Failed to get or create chat: " + e.getMessage());
        } finally {
            entityManager.close();
        }
        return chat;
    }

    private Chat findChatByOrder(FoodOrder managedOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Chat> cq = cb.createQuery(Chat.class);
        Root<Chat> root = cq.from(Chat.class);
        cq.select(root).where(cb.equal(root.get("foodOrder"), managedOrder));

        List<Chat> results = entityManager.createQuery(cq).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private Chat createNewChat(FoodOrder managedOrder) {
        Chat chat = new Chat(
                "Order #" + managedOrder.getId() + " Chat",
                managedOrder
        );
        managedOrder.setChat(chat);
        entityManager.persist(chat);
        entityManager.merge(managedOrder);
        return chat;
    }

    public Chat getChatWithMessages(int chatId) {
        Chat chat = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            chat = entityManager.find(Chat.class, chatId);

            if (chat != null && chat.getMessages() != null) {
                chat.getMessages().size();
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Failed to fetch chat: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return chat;
    }

    public List<Review> getUserReviews(BasicUser user) {
        List<Review> reviews = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Review> query = cb.createQuery(Review.class);
            Root<Review> root = query.from(Review.class);

            query.select(root).where(cb.equal(root.get("reviewTarget"), user));

            query.orderBy(cb.desc(root.get("dateCreated")));

            reviews = entityManager.createQuery(query).getResultList();

            for (Review review : reviews) {
                if (review.getReviewOwner() != null) {
                    review.getReviewOwner().getName();
                    review.getReviewOwner().getSurname();
                }

                if (review.getFoodOrder() != null) {
                    review.getFoodOrder().getId();
                }
            }

        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error retrieving reviews", null, "Failed to load user reviews: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return reviews;
    }

    public Review getReviewById(int reviewId) {
        Review review = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            review = entityManager.find(Review.class, reviewId);

            if (review != null) {
                review.getReviewOwner().getName();
                review.getReviewTarget().getName();
                review.getFoodOrder().getId();
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            generateAlert(Alert.AlertType.ERROR, "Error retrieving review", null, "Failed to fetch review: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return review;
    }

    public void updateReview(int reviewId, int rating, String reviewText) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Review managedReview = entityManager.find(Review.class, reviewId);

            if (managedReview != null) {
                managedReview.setRating(rating);
                managedReview.setReviewText(reviewText);

                entityManager.merge(managedReview);

                entityManager.getTransaction().commit();

                generateAlert(Alert.AlertType.INFORMATION, "Success", null, "Review updated successfully!");
            } else {
                entityManager.getTransaction().rollback();
                generateAlert(Alert.AlertType.WARNING, "Not Found", null, "Review not found!");
            }
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            generateAlert(Alert.AlertType.ERROR, "Error updating review", null, "Failed to update review: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public void createReview(Review review) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            BasicUser managedOwner = entityManager.merge(review.getReviewOwner());
            BasicUser managedTarget = entityManager.merge(review.getReviewTarget());
            FoodOrder managedOrder = entityManager.merge(review.getFoodOrder());

            Review newReview = new Review(
                    review.getRating(),
                    review.getReviewText(),
                    managedOwner,
                    managedTarget,
                    managedOrder
            );

            entityManager.persist(newReview);

            managedOwner.getMyReviews().add(newReview);
            managedTarget.getFeedback().add(newReview);

            entityManager.getTransaction().commit();

            generateAlert(Alert.AlertType.INFORMATION, "Success", null, "Review created successfully!");
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            generateAlert(Alert.AlertType.ERROR, "Error creating review", null, "Failed to create review: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public boolean reviewExistsForOrder(int id) {
        boolean exists = false;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Review> root = query.from(Review.class);

            query.select(cb.count(root));
            query.where(cb.equal(root.get("foodOrder").get("id"), id));

            Long count = entityManager.createQuery(query).getSingleResult();
            exists = count > 0;
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error checking review", null,
                    "Failed to check if review exists: " + e.getMessage());
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return exists;
    }
}
