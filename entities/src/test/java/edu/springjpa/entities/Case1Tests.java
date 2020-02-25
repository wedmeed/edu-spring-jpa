package edu.springjpa.entities;

import edu.springjpa.entities.c_1_no_relation.dao.Cookie1Repository;
import edu.springjpa.entities.c_1_no_relation.dao.User1Repository;
import edu.springjpa.entities.c_1_no_relation.models.Cookie1;
import edu.springjpa.entities.c_1_no_relation.models.User1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        crepo.flush();
        urepo.deleteAll();
        urepo.flush();

    }

    @Test
    public void testCanReadData() {
        Cookie1 goodOne = crepo.save(new Cookie1(null, "good"));
        User1 fedor = urepo.save(new User1(null, "Fedor"));

        assertNotNull(goodOne);
        assertNotNull(goodOne.getId());
        assertNotNull(fedor);
        assertNotNull(fedor.getId());

        assertTrue(crepo.findById(goodOne.getId()).isPresent());
        assertTrue(urepo.findById(fedor.getId()).isPresent());
    }

    @Test
    public void testCanDeleteData() {
        Cookie1 goodOne = crepo.save(new Cookie1(null, "good"));
        Cookie1 badOne = crepo.save(new Cookie1(null, "bad"));
        Cookie1 sosoOne = crepo.save(new Cookie1(null, "so-so"));
        User1 fedor = urepo.save(new User1(null, "Fedor"));
        User1 kesha = urepo.save(new User1(null, "Enokentiy"));
        User1 jora = urepo.save(new User1(null, "George"));
        crepo.flush();
        urepo.flush();


        crepo.delete(goodOne);
        assertFalse(crepo.findById(goodOne.getId()).isPresent());
        assertTrue(crepo.findById(sosoOne.getId()).isPresent());
        urepo.delete(fedor);
        assertFalse(urepo.findById(fedor.getId()).isPresent());
        assertTrue(urepo.findById(jora.getId()).isPresent());
        crepo.flush();
        urepo.flush();

        crepo.deleteById(badOne.getId());
        assertFalse(crepo.findById(badOne.getId()).isPresent());
        assertTrue(crepo.findById(sosoOne.getId()).isPresent());
        urepo.deleteById(kesha.getId());
        assertFalse(urepo.findById(kesha.getId()).isPresent());
        assertTrue(urepo.findById(jora.getId()).isPresent());
        crepo.flush();
        urepo.flush();

        crepo.deleteAll();
        assertFalse(crepo.findById(sosoOne.getId()).isPresent());
        urepo.deleteAll();
        assertFalse(urepo.findById(jora.getId()).isPresent());
        crepo.flush();
        urepo.flush();

        assertEquals(crepo.count(), 0);
        assertEquals(urepo.count(), 0);
    }


}