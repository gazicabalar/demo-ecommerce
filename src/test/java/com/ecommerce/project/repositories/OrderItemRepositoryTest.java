package com.ecommerce.project.repositories;

import com.ecommerce.project.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class OrderItemRepositoryTest {
        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private OrderItemRepository orderItemRepository;

        private OrderItem orderItem1;
        private OrderItem orderItem2;
        private Product product1;
        private Product product2;
        private Order order1;
        private Order order2;
        private Category category;
        private User user1;
        private User user2;

        @BeforeEach
        void setUp(){
            user1 = new User();
            user1.setUserName("user1");
            user1.setPassword("password1");
            user1.setEmail("user1@testmail.com");

            user2 = new User();
            user2.setUserName("user2");
            user2.setPassword("password2");
            user2.setEmail("user2@testmail.com");

            category = new Category();
            category.setCategoryName("category1");

            product1 = new Product();
            product1.setProductName("product1");
            product1.setDescription("product1 description");
            product1.setPrice(100.0);
            product1.setQuantity(10);
            product1.setDiscount(5.0);
            product1.setSpecialPrice(95.0);
            product1.setCategory(category);
            product1.setUser(user1);

            product2 = new Product();
            product2.setProductName("product2");
            product2.setDescription("product2 description");
            product2.setPrice(200.0);
            product2.setQuantity(20);
            product2.setDiscount(10.0);
            product2.setSpecialPrice(180.0);
            product2.setCategory(category);
            product2.setUser(user2);

            order1 = new Order();
            order1.setEmail("email1@testmail.com");
            order1.setTotalAmount(100.0);

            order2 = new Order();
            order2.setEmail("email2@testmail.com");
            order2.setTotalAmount(200.0);

            orderItem1 = new OrderItem();
            orderItem1.setProduct(product1);
            orderItem1.setOrder(order1);
            orderItem1.setQuantity(10);
            orderItem1.setDiscount(5.0);
            orderItem1.setOrderedProductPrice(95.0);

            orderItem2 = new OrderItem();
            orderItem2.setProduct(product2);
            orderItem2.setOrder(order2);
            orderItem2.setQuantity(30);
            orderItem2.setDiscount(0);
            orderItem2.setOrderedProductPrice(180.0);
        }

        @Test
        @DisplayName("OrderItem Id, User Email ve User Name ile OrderItem Bulma - Başarılı")
        void testFindOrderItemIdAndUserEmailAndUserName(){
            entityManager.persist(category);
            entityManager.persist(user1);
            entityManager.persist(product1);
            entityManager.persist(order1);
            entityManager.persist(orderItem1);
            entityManager.flush();

            OrderItem foundOrderItem = orderItemRepository.findByOrderItemIdAndProduct_User_EmailAndProduct_User_UserName(orderItem1.getOrderItemId(), "user1@testmail.com", "user1");
            assertNotNull(foundOrderItem);
            assertEquals("user1@testmail.com", orderItem1.getProduct().getUser().getEmail());
            assertEquals("user1", orderItem1.getProduct().getUser().getUserName());
        }

        @Test
        @DisplayName("OrderItem Id, User Email ve User Name ile OrderItem Bulma - Başarısız")
        void testFindOrderItemIdAndUserEmailAndUserName_NotFound(){
            entityManager.persist(user1);
            entityManager.persist(category);
            entityManager.persist(product1);
            entityManager.persist(order1);
            entityManager.persist(orderItem1);
            entityManager.flush();

            OrderItem foundOrderItem = orderItemRepository.findByOrderItemIdAndProduct_User_EmailAndProduct_User_UserName(99L, "user1@testmail.com", "user1");
            assertNull(foundOrderItem);
        }


        @Test
        @DisplayName("OrderItem Id ile Order Email Bulma - Başarılı")
        void testFindByOrderItemIdAndOrderEmail(){
            entityManager.persist(user2);
            entityManager.persist(category);
            entityManager.persist(product2);
            entityManager.persist(order2);
            entityManager.persist(orderItem2);
            entityManager.flush();

            OrderItem foundOrderItem = orderItemRepository.findByOrderItemIdAndOrder_Email(orderItem2.getOrderItemId(), "email2@testmail.com");
            assertNotNull(foundOrderItem);
            assertEquals("email2@testmail.com", orderItem2.getOrder().getEmail());
        }

        @Test
        @DisplayName("OrderItem Id ile Order Email Bulma - Başarısız")
        void testFindByOrderItemIdAndOrderEmail_NotFound(){
            entityManager.persist(user2);
            entityManager.persist(category);
            entityManager.persist(product2);
            entityManager.persist(order2);
            entityManager.persist(orderItem2);
            entityManager.flush();

            OrderItem foundOrderItem = orderItemRepository.findByOrderItemIdAndOrder_Email(orderItem2.getOrderItemId(), "non_defined@testmail.com");
            assertNull(foundOrderItem);
        }

        @Test
        @DisplayName("Product a göre OrderItem Bulma - Başarılı")
        void testFindProductByName(){
            entityManager.persist(user1);
            entityManager.persist(category);
            entityManager.persist(product1);
            entityManager.persist(order1);
            entityManager.persist(orderItem1);
            entityManager.flush();

            OrderItem foundOrderItem = orderItemRepository.findByProduct_ProductName("product1");

            assertNotNull(foundOrderItem);
            assert(foundOrderItem.getProduct().getProductName().equals("product1"));
        }

        @Test
        @DisplayName("Product a göre OrderItem Bulma - Başarısız")
        void testFindProductByName_NotFound(){
            entityManager.persist(user1);
            entityManager.persist(category);
            entityManager.persist(product1);
            entityManager.persist(order1);
            entityManager.persist(orderItem1);
            entityManager.flush();

            OrderItem foundOrderItem = orderItemRepository.findByProduct_ProductName("product5");
            assertNull(foundOrderItem);
        }

        @Test
        @DisplayName("OrderItem Kaydetme")
        void testSaveOrderItem(){
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setProduct(product1);
            newOrderItem.setOrder(order1);

            OrderItem savedOrderItem = orderItemRepository.save(newOrderItem);
            assertNotNull(savedOrderItem);
            assert(savedOrderItem.getProduct().getProductName().equals("product1"));
            assert(savedOrderItem.getOrder().getEmail().equals("email1@testmail.com"));
        }

        @Test
        @DisplayName("OrderItem Silme")
        void testOrderItemDelete(){
            entityManager.persist(user1);
            entityManager.persist(category);
            entityManager.persist(product1);
            entityManager.persist(order1);

            OrderItem savedOrderItem = entityManager.persist(orderItem1);
            entityManager.flush();

            orderItemRepository.deleteById(orderItem1.getOrderItemId());
            entityManager.flush();
            entityManager.clear();

            Optional<OrderItem> foundOrderItem = orderItemRepository.findById(orderItem1.getOrderItemId());
            assertTrue(foundOrderItem.isEmpty());
        }

}