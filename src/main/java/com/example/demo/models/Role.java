package com.example.demo.models;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @SequenceGenerator(sequenceName = "ROLES_SEQ", allocationSize = 1, name = "ROLES_SEQ")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_permision",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permision_id"))
    private Set<Permission> permissions = new HashSet<>();

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}
