package edu.springjpa.entities.c_2_o2m_uni_lazy_column.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cookie2 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String taste;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User2 myLord;
    @ManyToOne(fetch = FetchType.LAZY)
    private User2 myOldLord;

}
