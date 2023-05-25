package kz.vlife.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.vlife.demo.dto.CategoryDto;
import kz.vlife.demo.service.CategoryService;
import kz.vlife.demo.service.LocaleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "API for category operations")
public class CategoryControllerV1 {

    private final CategoryService categoryService;
    private final LocaleService localeService;


    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestHeader(name = "Accept-Language", defaultValue = "ru", required = false) Optional<String> language,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "depth", required = false, defaultValue = "0") int depth
    ) {
        String lang = localeService.determineLanguage(language);
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDto> categories = null;
        if (depth != 0) {
            categories = categoryService.getAllCategories(name, pageable, lang, depth);
        } else {
            categories = categoryService.getAllCategories(name, pageable, lang);
        }
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(
            @Parameter(description = "ID of the category") @PathVariable Integer id,
            @RequestHeader("Accept-Language") Optional<String> language) {
        String lang = localeService.determineLanguage(language);
        CategoryDto category = categoryService.getCategory(id, lang);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto, "ru");
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing category")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto, @RequestHeader("Accept-Language") Optional<String> language) {
        String lang = localeService.determineLanguage(language);
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto, lang);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Add a child to a parent category")
    @GetMapping("/{parentId}/child")
    public ResponseEntity<CategoryDto> getChildren(
            @RequestHeader(name = "Accept-Language", defaultValue = "ru", required = false) Optional<String> language,
            @PathVariable Integer parentId) {
        String lang = localeService.determineLanguage(language);
        return ResponseEntity.ok(categoryService.getChildren(parentId, lang));
    }

    @Operation(summary = "Add a child to a parent category")
    @PostMapping("/{parentId}/child/{childId}")
    public ResponseEntity<Void> addChild(@PathVariable Integer parentId, @PathVariable Integer childId) {
        categoryService.addChildToParent(parentId, childId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Set a parent for a category")
    @PostMapping("/{categoryId}/parent/{parentId}")
    public ResponseEntity<Void> setParentCategory(@PathVariable Integer categoryId, @PathVariable Integer parentId) {
        categoryService.addChildToParent(categoryId, parentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Integer id,
            @RequestParam(name = "force", defaultValue = "false", required = false) boolean isForce) {
        try {
            if (isForce) {
                categoryService.forceDeleteCategory(id);
            } else {
                categoryService.deleteCategory(id);
            }
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
