package com.atilimited.fieldnotes.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String passwordHash; 

    @Column(nullable = false, length = 120)
    private String name;
}
