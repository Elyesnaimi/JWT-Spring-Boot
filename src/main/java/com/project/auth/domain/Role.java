package com.project.auth.domain;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import java.util.Objects;

import static javax.persistence.EnumType.STRING;

@Entity
public class Role extends Identity {

    @Enumerated(STRING)
    @NaturalId
    @Column(length = 60)
    private RoleName name;

    private Role() {

    }

    public RoleName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id == role.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}