package kz.vlife.demo.repository;

import kz.vlife.demo.entity.Category;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Component
public class TreeCategoryRepositoryImpl implements TreeCategoryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Category> findTree(String name, int depth) {
        String sql = "WITH RECURSIVE category_tree AS (" +
                "  SELECT c.id, c.default_name, cr.parent_id, 1 AS depth " +
                "  FROM categories c " +
                "  LEFT JOIN category_relations cr ON c.id = cr.child_id " +
                "  WHERE cr.parent_id IS NULL " +
                "  UNION ALL " +
                "  SELECT c.id, c.default_name, cr.parent_id, ct.depth + 1 " +
                "  FROM category_tree ct JOIN category_relations cr ON ct.id = cr.child_id " +
                "  JOIN categories c ON cr.child_id = c.id " +
                "  WHERE ct.depth < :depth)" +
                "SELECT * FROM category_tree";


        Query query = em.createNativeQuery(sql, Category.class);
        query.setParameter("depth", depth);
        return query.getResultList();
    }

    @Override
    public Optional<Category> findChildren(Integer parentId) {
        String sql = "WITH RECURSIVE category_tree AS (" +
                "  SELECT c.id, c.default_name, cr.parent_id, 1 AS depth " +
                "  FROM categories c " +
                "  LEFT JOIN category_relations cr ON c.id = cr.child_id " +
                "  WHERE cr.parent_id IS NULL AND c.id = :parentId" +
                "  UNION ALL " +
                "  SELECT c.id, c.default_name, cr.parent_id, ct.depth + 1 " +
                "  FROM category_tree ct JOIN category_relations cr ON ct.id = cr.child_id " +
                "  JOIN categories c ON cr.child_id = c.id) " +
                "SELECT * FROM category_tree";


        Query query = em.createNativeQuery(sql, Category.class);
        query.setParameter("parentId", parentId);
        return Optional.of((Category) query.getSingleResult());
    }

}
