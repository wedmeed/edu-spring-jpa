package edu.springjpa.entities.c_1_no_relation.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cookie1 {

    @Id
    private Long id;
    private String taste;

}
