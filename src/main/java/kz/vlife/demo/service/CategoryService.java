package kz.vlife.demo.service;

import kz.vlife.demo.dto.CategoryDto;
import kz.vlife.demo.entity.Category;
import kz.vlife.demo.entity.CategoryRelation;
import kz.vlife.demo.entity.CategoryTranslation;
import kz.vlife.demo.exception.NotFoundException;
import kz.vlife.demo.mapper.CategoryMapper;
import kz.vlife.demo.repository.CategoryRelationRepository;
import kz.vlife.demo.repository.CategoryRepository;
import kz.vlife.demo.repository.CategoryTranslationRepository;
import kz.vlife.demo.repository.TreeCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TreeCategoryRepository treeCategoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;
    private final CategoryRelationRepository categoryRelationRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDto> getAllCategories(String name, Pageable pageable, String locale) {
        return categoryRepository.findAll(
                        Specification.where(nameContains(name)), pageable
                )
                .map(category -> categoryMapper.toDto(category, locale));
    }

    @Transactional(readOnly = true)
    public Page<CategoryDto> getAllCategories(String name, Pageable pageable, String locale, int depth) {
        return new PageImpl(
                treeCategoryRepository.findTree(name, depth)
                        .stream().map(category -> categoryMapper.toDto(category, locale, true))
                        .collect(Collectors.toList()),
                pageable,
                0L);
    }

    @Transactional(readOnly = true)
    public CategoryDto getChildren(Integer parentId, String locale) {
        return treeCategoryRepository.findChildren(parentId)
                .map(category -> categoryMapper.toDto(category, locale, true))
                .orElseThrow(NotFoundException::new);
    }

    private Specification<Category> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null) {
                return cb.isTrue(cb.literal(true)); // always true = no filtering
            }
            return cb.like(root.get("defaultName"), "%" + name + "%");
        };
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Integer id, String locale) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDto(category, locale);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto, String locale) {
        Category category = categoryMapper.toEntity(categoryDto);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category, locale);
    }

    @Transactional
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto, String locale) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if ("ru".equals(locale)) {
            category.setDefaultName(categoryDto.getName());
        } else {
            CategoryTranslation categoryTranslation = this.categoryTranslationRepository.findByCategoryAndLanguage(category, locale)
                    .orElseGet(CategoryTranslation::new);

            categoryTranslation.setCategory(category);
            categoryTranslation.setLanguage(locale);
            categoryTranslation.setName(categoryDto.getName());
            categoryTranslationRepository.save(categoryTranslation);
        }
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category, locale);
    }

    @Transactional
    public void addChildToParent(Integer parentId, Integer childId) {
        Category parent = categoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException("Parent category not found"));
        Category child = categoryRepository.findById(childId).orElseThrow(() -> new NotFoundException("Child category not found"));

        CategoryRelation relation = new CategoryRelation();
        relation.setParent(parent);
        relation.setChild(child);

        categoryRelationRepository.save(relation);
    }


    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found."));

        categoryRepository.delete(category);
    }

    public void forceDeleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found."));

        if (!category.getChildren().isEmpty()) {
            for (CategoryRelation child : category.getChildren()) {
                forceDeleteCategory(child.getChild().getId());
            }
        }
        categoryRepository.delete(category);
    }
}
