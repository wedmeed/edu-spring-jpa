package edu.springjpa.entities.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cookie1 {

    @Id
    private Long id;
    private String taste;

}
