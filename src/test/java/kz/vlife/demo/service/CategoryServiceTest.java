package kz.vlife.demo.service;

import kz.vlife.demo.mapper.CategoryMapper;
import kz.vlife.demo.repository.CategoryRepository;
import kz.vlife.demo.repository.CategoryTranslationRepository;
import kz.vlife.demo.repository.TreeCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private TreeCategoryRepository treeCategoryRepository;

    @MockBean
    private CategoryTranslationRepository categoryTranslationRepository;

    @Test
    public void testGetAllCategories() {
        // Arrange
        String name = "test";
        Pageable pageable = PageRequest.of(0, 10);
        String locale = "en";

        var categories = categoryService.getAllCategories(name, pageable, locale);

        assertNotNull(categories, "The returned list should not be null");
        assertEquals(1, categories.getTotalElements(), "The page should contain one element");
    }
}
