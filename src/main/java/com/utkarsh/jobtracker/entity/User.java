package com.utkarsh.jobtracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.usertype.UserType;

import javax.management.relation.Role;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table (name = "user")
public class User {
 @Id
 @GeneratedValue (strategy = GenerationType.UUID)
    private String id;

 @Column(unique = true ,nullable=false)
    private String email;

 @com.fasterxml.jackson.annotation.JsonIgnore
 @Column(nullable = false)
    private String passwordHash;

 @Enumerated(EnumType.STRING)
 private Role role=Role.USER;

 private Instant creationAt = Instant.now();

 private enum Role{
     USER, ADMIN
 }
}
