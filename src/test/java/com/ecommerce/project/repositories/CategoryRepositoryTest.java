package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private User  user1;
    private User user2;

    @BeforeEach
    void setUp() {
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

        category1 = new Category();
        category1.setCategoryName("Electronics");
        testEntityManager.persistAndFlush(category1);

        category2 = new Category();
        category2.setCategoryName("Books");
        testEntityManager.persistAndFlush(category2);
        
        product1 = new Product();
        product1.setProductName("Telephone");
        product1.setPrice(100.0);
        product1.setDescription("Smartphone with 4GB RAM");
        product1.setUser(user1);
        product1.setCategory(category1);
        testEntityManager.persistAndFlush(product1);

        product2 = new Product();
        product2.setProductName("HP Notebook");
        product2.setPrice(500.0);
        product2.setDescription("PC with 12GB RAM");
        product2.setUser(user2);
        product2.setCategory(category1);
        testEntityManager.persistAndFlush(product2);

        product3 = new Product();
        product3.setProductName("Book1");
        product3.setPrice(300.0);
        product3.setDescription("Book1 description");
        product3.setUser(user1);
        product3.setCategory(category2);
        testEntityManager.persistAndFlush(product3);

        product4 = new Product();
        product4.setProductName("Book2");
        product4.setPrice(300.0);
        product4.setDescription("Book2 description");
        product4.setUser(user2);
        product4.setCategory(category2);
        testEntityManager.persistAndFlush(product4);
    }

    @Test
    @DisplayName("Kategori adına göre bul - Başarılı")
    void testFindCategoryByName() {
        Category foundCategory = categoryRepository.findByCategoryName("Electronics");
        assertNotNull(foundCategory);
        assertEquals("Electronics", foundCategory.getCategoryName());
    }

    @Test
    @DisplayName("Kategori adına göre bul - Başarısız")
    void testFindCategoryByName_NotFound() {
        Category foundCategory = categoryRepository.findByCategoryName("Construction");
        assertNull(foundCategory);
    }

    @Test
    @DisplayName("Ürün adı ve kategori adına göre kategoriyi bul - Başarılı")
    void testFindProductNameAndCategoryName_Success() {
        Category foundCategory = categoryRepository.findByProductNameAndCategoryName("Telephone", "Electronics");
        assertNotNull(foundCategory);
        assertEquals("Electronics", foundCategory.getCategoryName());
    }

    @Test
    @DisplayName("Ürün adı ve kategori adına göre kategoriyi bul - Başarısız")
    void testFindProductNameAndCategoryName_NotFound() {
        // Telephone ürünü Books kategorisine ait değil
        Category foundCategory = categoryRepository.findByProductNameAndCategoryName("Telephone", "Books");
        
        assertNull(foundCategory);
    }

    @Test
    @DisplayName("Kategori adı, kullanıcı email ve kullanıcı adına göre bul - Başarılı")
    void testFindByCategoryAndUserEmailAndUserName_Success() {
        Category foundCategory = categoryRepository.findByCategoryAndUserEmailAndUserName("Electronics", "testuser1@example.com", "testuser1");
        assertNotNull(foundCategory);
        assertEquals("Electronics", foundCategory.getCategoryName());
    }

    @Test
    @DisplayName("Kategori adı, kullanıcı email ve kullanıcı adına göre bul - Başarısız (Yanlış Email)")
    void testFindByCategoryAndUserEmailAndUserName_WrongEmail() {
        Category foundCategory = categoryRepository.findByCategoryAndUserEmailAndUserName("Electronics", "wrong@example.com", "testuser1");
        assertNull(foundCategory);
    }

    @Test
    @DisplayName("Yeni kategori kaydetme testi")
    void testSaveCategory() {
        Category newCategory = new Category();
        newCategory.setCategoryName("Cosmetics");
        
        Category savedCategory = categoryRepository.save(newCategory);
        
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Cosmetics", savedCategory.getCategoryName());
    }

    @Test
    @DisplayName("Kategori silme testi")
    void testDeleteCategory() {
        categoryRepository.deleteById(category1.getCategoryId());
        Optional<Category> foundCategory = categoryRepository.findById(category1.getCategoryId());
        assertTrue(foundCategory.isEmpty());
    }

    @Test
    @DisplayName("Kategori güncelleme testi")
    void testUpdateCategory() {
        category1.setCategoryName("Gadgets");
        categoryRepository.save(category1);
        
        Category updatedCategory = categoryRepository.findById(category1.getCategoryId()).orElse(null);
        assertNotNull(updatedCategory);
        assertEquals("Gadgets", updatedCategory.getCategoryName());
    }
}
