package kz.vlife.demo.repository;

import kz.vlife.demo.entity.Category;

import java.util.List;
import java.util.Optional;

public interface TreeCategoryRepository {
    List<Category> findTree(String name, int depth);

    Optional<Category> findChildren(Integer parentId);
}
