package edu.springjpa.entities.c_1_no_relation.dao;

import edu.springjpa.entities.c_1_no_relation.models.Cookie1;
import edu.springjpa.entities.c_1_no_relation.models.User1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class Case1Tests {

    @Autowired
    private Cookie1Repository crepo;
    @Autowired
    private User1Repository urepo;

    @BeforeEach
    public void setUp() {
        crepo.deleteAll();
        urepo.deleteAll();

        crepo.save(new Cookie1(1L, "good"));
        crepo.save(new Cookie1(2L, "bad"));
        urepo.save(new User1(1L, "Fedor"));
        urepo.save(new User1(2L, "Enokentiy"));
    }

    @Test
    public void testCanReadData() {
        assertTrue(crepo.findById(1L).isPresent());
        assertTrue(crepo.findById(2L).isPresent());
        assertTrue(urepo.findById(1L).isPresent());
        assertTrue(urepo.findById(2L).isPresent());
    }


}