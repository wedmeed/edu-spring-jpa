package edu.springjpa.entities;

import edu.springjpa.entities.c_2_o2m_uni_lazy_column.dao.Cookie2Repository;
import edu.springjpa.entities.c_2_o2m_uni_lazy_column.dao.User2Repository;
import edu.springjpa.entities.c_2_o2m_uni_lazy_column.models.Cookie2;
import edu.springjpa.entities.c_2_o2m_uni_lazy_column.models.User2;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class Case2Tests {

    @Autowired
    private Cookie2Repository crepo;
    @Autowired
    private User2Repository urepo;


    @BeforeEach
    void setUp() {
        crepo.deleteAll();
        crepo.flush();
        urepo.deleteAll();
        urepo.flush();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveSeparetellyWithManagedEntity() {
        User2 fedor = urepo.save(new User2(null, "Fedor"));
        User2 kesha = urepo.save(new User2(null, "Enokentiy"));
        urepo.flush();

        //you need to use the returned proxy instance to arrange relations with saved entities
        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", null, null));
        goodOne.setMyLord(fedor);
        goodOne.setMyOldLord(kesha);
        goodOne = crepo.save(goodOne);
        Cookie2 badOne = crepo.save(new Cookie2(null, "bad", null, null));
        badOne.setMyLord(fedor);
        crepo.save(badOne);
        crepo.flush();

        Optional<Cookie2> goodOneFromDB = crepo.findById(goodOne.getId());
        assertTrue(goodOneFromDB.isPresent());
        assertEquals(2, crepo.countByMyLord_Id(fedor.getId()));
        assertEquals(1, crepo.countByMyOldLord_Id(kesha.getId()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveSeparetellyForUnmanagedEntity() {
        User2 fedor = urepo.save(new User2(null, "Fedor"));
        User2 kesha = urepo.save(new User2(null, "Enokentiy"));
        urepo.flush();

        // you cannot use an unmanaged instance to arrange relations
        // with saved entities
        assertThrows(InvalidDataAccessApiUsageException.class, () ->
                crepo.save(new Cookie2(null, "good", fedor, null)));


        // but you can use an unmanaged instance to arrange relations
        // with saved entities if there is no cascading
        Cookie2 badOne = crepo.save(new Cookie2(null, "bad", null, kesha));
        crepo.flush();

        Optional<Cookie2> badOneFromDB = crepo.findById(badOne.getId());
        assertTrue(badOneFromDB.isPresent());
        assertEquals(1, crepo.countByMyOldLord_Id(kesha.getId()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveByRelationWithUnmanagedEntity() {
        User2 fedor = new User2(null, "Fedor");
        User2 kesha = new User2(null, "Enokentiy");

        // you can use an unmanaged instance to arrange relations
        // with unsaved entities if a used cascadeType supports it
        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", fedor, null));
        urepo.flush();
        crepo.flush();

        Optional<User2> fedorFromDB = urepo.findFirstByName(fedor.getName());
        Optional<Cookie2> goodOneFromDB = crepo.findById(goodOne.getId());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());

        // you cannot use an unmanaged instance to arrange relations
        // with unsaved entities if a used cascadeType doesn't support it
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> crepo.save(new Cookie2(null, "bad", null, kesha)));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveByRelationWithManagedEntity() {
        User2 fedor = new User2(null, "Fedor");
        User2 kesha = new User2(null, "Enokentiy");

        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", null, null));
        crepo.flush();

        // you can use a managed proxy instance to arrange relations
        // with unsaved entities if a used cascadeType supports it
        goodOne.setMyLord(fedor);
        goodOne = crepo.save(goodOne);
        urepo.flush();
        crepo.flush();

        Optional<User2> fedorFromDB = urepo.findFirstByName(fedor.getName());
        Optional<Cookie2> goodOneFromDB = crepo.findById(goodOne.getId());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());

        // you cannot use a managed proxy instance to arrange relations
        // with unsaved entities if a used cascadeType doesn't support it
        goodOne.setMyOldLord(kesha);
        Cookie2 goodOneForCheck = goodOne;
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> crepo.save(goodOneForCheck));

    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testCanReadUnderTransaction() {
        User2 fedor = new User2(null, "Fedor");
        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", fedor, null));

        Optional<User2> fedorFromDB = urepo.findById(fedor.getId());
        Optional<Cookie2> goodOneFromDB = crepo.findById(goodOne.getId());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());
        assertNotNull(goodOneFromDB.get().getMyLord());
        assertEquals(fedorFromDB.get(), goodOneFromDB.get().getMyLord());

    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCannotReadWithoutTransaction() {
        User2 fedor = new User2(null, "Fedor");
        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", fedor, null));

        Optional<User2> fedorFromDB = urepo.findById(fedor.getId());
        Optional<Cookie2> goodOneFromDB = crepo.findById(goodOne.getId());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());
        assertThrows(LazyInitializationException.class, () -> goodOneFromDB.get().getMyLord().getName());
    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanDeleteData() {
        User2 fedor = new User2(null, "Fedor");
        User2 kesha = new User2(null, "Enokentiy");
        User2 jora = new User2(null, "George");
        crepo.save(new Cookie2(null, "good", fedor, null));
        crepo.save(new Cookie2(null, "bad", kesha, null));
        crepo.save(new Cookie2(null, "so-so", jora, null));
        crepo.flush();
        urepo.flush();


        crepo.deleteAll();
        urepo.deleteAll();
        crepo.flush();
        urepo.flush();

        assertEquals(0, crepo.count());
        assertEquals(0, urepo.count());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCannotDeleteSeparately() {
        User2 fedor = urepo.save(new User2(null, "Fedor"));
        User2 kesha = urepo.save(new User2(null, "Enokentiy"));
        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", null, null));
        Cookie2 badOne = crepo.save(new Cookie2(null, "bad", null, null));
        goodOne.setMyLord(fedor);
        badOne.setMyOldLord(kesha);
        crepo.save(goodOne);
        crepo.save(badOne);
        crepo.flush();
        urepo.flush();

        // cannot delete a not-owner entity because of the foreign key constraints
        assertThrows(DataIntegrityViolationException.class,
                () -> urepo.delete(fedor));
        assertThrows(DataIntegrityViolationException.class,
                () -> urepo.delete(kesha));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanDeleteByRelations() {
        User2 fedor = urepo.save(new User2(null, "Fedor"));
        User2 kesha = urepo.save(new User2(null, "Enokentiy"));
        Cookie2 goodOne = crepo.save(new Cookie2(null, "good", null, null));
        Cookie2 badOne = crepo.save(new Cookie2(null, "bad", null, null));
        goodOne.setMyLord(fedor);
        badOne.setMyOldLord(kesha);
        goodOne = crepo.save(goodOne);
        badOne = crepo.save(badOne);
        crepo.flush();
        urepo.flush();

        // deletes related entity if it's supported by the used cascadeType
        crepo.delete(goodOne);
        crepo.flush();
        urepo.flush();
        assertFalse(crepo.findById(goodOne.getId()).isPresent());
        assertFalse(urepo.findById(fedor.getId()).isPresent());

        // doesn't delete related entity if it isn't supported by the used cascadeType
        crepo.deleteById(badOne.getId());
        crepo.flush();
        urepo.flush();
        assertFalse(crepo.findById(badOne.getId()).isPresent());
        assertTrue(urepo.findById(kesha.getId()).isPresent());

    }


}