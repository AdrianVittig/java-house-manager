package org.university.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public final boolean equals(Object o){
        if(this == o) return true;
        if(o == null) return false;
        if(!(o instanceof BaseEntity other)) return false;
        if(org.hibernate.Hibernate.getClass(this) != org.hibernate.Hibernate.getClass(o)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public final int hashCode(){
        return org.hibernate.Hibernate.getClass(this).hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + getId() + "}";
    }
}
