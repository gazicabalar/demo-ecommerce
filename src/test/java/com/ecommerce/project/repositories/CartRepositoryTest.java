package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    
    @Autowired
    private CartRepository cartRepository;
    
    private Cart cart1;
    private Cart cart2;
    private User user1;
    private User user2;
    private Product product1;
    private Product product2;
    private CartItem cartItem1;
    private CartItem cartItem2;
        
    @BeforeEach
    void setUp(){

        user1 = new User();
        user1.setUserName("testuser1");
        user1.setEmail("testuser1@example.com");
        user1.setPassword("password123");
        testEntityManager.persistAndFlush(user1);

        user2 = new User();
        user2.setUserName("testuser2");
        user2.setEmail("testuser2@example.com");
        user2.setPassword("password456");
        testEntityManager.persistAndFlush(user2);

        cart1 = new Cart();
        cart1.setUser(user1);
        cart1.setTotalPrice(0.0);
        testEntityManager.persistAndFlush(cart1);

        cart2 = new Cart();
        cart2.setUser(user2);
        cart2.setTotalPrice(0.0);
        testEntityManager.persistAndFlush(cart2);

        product1 = new Product();
        product1.setProductName("Product 1");
        product1.setPrice(100.0);
        product1.setDescription("Description for Product 1");
        testEntityManager.persistAndFlush(product1);

        product2 = new Product();
        product2.setProductName("Product 2");
        product2.setPrice(200.0);
        product2.setDescription("Description for Product 2");
        testEntityManager.persistAndFlush(product2);

        cartItem1 = new CartItem();
        cartItem1.setCart(cart1);
        cartItem1.setProduct(product1);
        cartItem1.setQuantity(2);
        cartItem1.setDiscount(0.0);
        cart1.getCartItems().add(cartItem1);
        testEntityManager.persistAndFlush(cartItem1);

        cartItem2 = new CartItem();
        cartItem2.setCart(cart1);
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(1);
        cartItem2.setDiscount(0.0);
        cart1.getCartItems().add(cartItem2);
        testEntityManager.persistAndFlush(cartItem2);
        
        testEntityManager.clear();
    }
    
    @Test
    @DisplayName("Email'e göre sepeti bul - Başarılı")
    void testFindByCartEmail_Success() {
        Cart foundCart = cartRepository.findByCartEmail("testuser1@example.com");

        assertNotNull(foundCart);
        assertEquals(cart1.getCartId(), foundCart.getCartId());
        assertEquals("testuser1@example.com", foundCart.getUser().getEmail());
    }
    
    @Test
    @DisplayName("Email'e göre sepeti bul - Bulunmayan email")
    void testFindByCartEmail_NotFound() {
        Cart foundCart = cartRepository.findByCartEmail("nonexistent@example.com");

        assertNull(foundCart);
    }
    
    @Test
    @DisplayName("Email ve CartId'ye göre sepeti bul - Başarılı")
    void testFindByCartEmailAndCartId_Success() {
        Cart foundCart = cartRepository.findByCartEmailAndCartId("testuser1@example.com", cart1.getCartId());

        assertNotNull(foundCart);
        assertEquals(cart1.getCartId(), foundCart.getCartId());
        assertEquals("testuser1@example.com", foundCart.getUser().getEmail());
    }
    
    @Test
    @DisplayName("Email ve CartId'ye göre sepeti bul - Yanlış CartId")
    void testFindByCartEmailAndCartId_WrongCartId() {
        Cart foundCart = cartRepository.findByCartEmailAndCartId("testuser1@example.com", 99999L);

        assertNull(foundCart);
    }
    
    @Test
    @DisplayName("Ürün ID'sine göre sepetleri bul - Başarılı")
    void testFindCartsByProductId_Success() {
        List<Cart> carts = cartRepository.findCartsByProductId(product1.getProductId());

        assertNotNull(carts);
        assertFalse(carts.isEmpty());
        assertEquals(1, carts.size());
        assertEquals(cart1.getCartId(), carts.get(0).getCartId());
    }
    
    @Test
    @DisplayName("Ürün ID'sine göre sepetleri bul - Bulunmayan ürün")
    void testFindCartsByProductId_NoProductFound() {
        List<Cart> carts = cartRepository.findCartsByProductId(99999L);

        assertNotNull(carts);
        assertTrue(carts.isEmpty());
    }
    
    @Test
    @DisplayName("JpaRepository - Tüm sepetleri listele")
    void testFindAll() {
        List<Cart> carts = cartRepository.findAll();

        assertNotNull(carts);
        assertTrue(carts.size() >= 2);
    }
    
    @Test
    @DisplayName("JpaRepository - ID'ye göre sepeti bul")
    void testFindById() {
        Optional<Cart> foundCart = cartRepository.findById(cart1.getCartId());

        assertTrue(foundCart.isPresent());
        assertEquals(cart1.getCartId(), foundCart.get().getCartId());
    }
    
    @Test
    @DisplayName("JpaRepository - Sepeti kaydet")
    void testSave() {
        User user3 = new User();
        user3.setUserName("testuser3");
        user3.setEmail("testuser3@example.com");
        user3.setPassword("password789");
        testEntityManager.persistAndFlush(user3);
        
        Cart newCart = new Cart();
        newCart.setUser(user3);
        newCart.setTotalPrice(50.0);

        Cart savedCart = cartRepository.save(newCart);

        assertNotNull(savedCart);
        assertNotNull(savedCart.getCartId());
        assertEquals(50.0, savedCart.getTotalPrice());
    }
    
    @Test
    @DisplayName("JpaRepository - Sepeti sil")
    void testDeleteById() {
        cartRepository.deleteById(cart2.getCartId());
        Optional<Cart> deletedCart = cartRepository.findById(cart2.getCartId());

        assertTrue(deletedCart.isEmpty());
    }
}