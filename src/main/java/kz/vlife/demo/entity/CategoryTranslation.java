package kz.vlife.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category_translations")
public class CategoryTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false, name = "translated_name")
    private String name;

    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private Category category;
}
