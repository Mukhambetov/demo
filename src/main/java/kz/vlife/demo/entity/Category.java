package kz.vlife.demo.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "default_name", nullable = false)
    private String defaultName;

    @OneToMany(mappedBy = "parent")
    private List<CategoryRelation> parents = new ArrayList<>();

    @OneToMany(mappedBy = "child")
    private List<CategoryRelation> children = new ArrayList<>();


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryTranslation> translations;
}
