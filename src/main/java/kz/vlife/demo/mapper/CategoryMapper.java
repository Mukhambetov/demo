package kz.vlife.demo.mapper;

import kz.vlife.demo.entity.Category;
import kz.vlife.demo.dto.CategoryDto;
import kz.vlife.demo.entity.CategoryTranslation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category, String language) {
        // Default to the category's default name
        String name = category.getDefaultName();

        // If a translation exists for the requested language, use it instead
        Optional<CategoryTranslation> translation = category.getTranslations().stream()
                .filter(t -> t.getLanguage().equals(language))
                .findFirst();
        if (translation.isPresent()) {
            name = translation.get().getName();
        }

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(name);

        return dto;
    }

    public CategoryDto toDto(Category category, String language, boolean addChildren) {
        CategoryDto dto = this.toDto(category, language);
        if (addChildren) {
            List<CategoryDto> children = category.getParents().stream()
                    .map(relation -> toDto(relation.getChild(), language, true))
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        return dto;
    }

    public Category toEntity(CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setDefaultName(dto.getName());

        // For simplicity, children are not handled here.
        // Depending on your use case, you might want to map them too.

        return category;
    }
}
