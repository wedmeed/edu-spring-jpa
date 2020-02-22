package edu.springjpa.entities.models;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User1 {

    @Id
    private Long id;
    private String name;
}
