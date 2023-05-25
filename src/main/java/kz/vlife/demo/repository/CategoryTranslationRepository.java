package kz.vlife.demo.repository;

import kz.vlife.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kz.vlife.demo.entity.CategoryTranslation;

import java.util.Optional;

@Repository
public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, Long> {
    Optional<CategoryTranslation> findByCategoryAndLanguage(Category category, String locale);
}
