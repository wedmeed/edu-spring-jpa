package edu.springjpa.entities;

import edu.springjpa.entities.c_3_o2m_uni_lazy_table.dao.Cookie3Repository;
import edu.springjpa.entities.c_3_o2m_uni_lazy_table.dao.User3Repository;
import edu.springjpa.entities.c_3_o2m_uni_lazy_table.models.Cookie3;
import edu.springjpa.entities.c_3_o2m_uni_lazy_table.models.User3;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class Case3Tests {

    @Autowired
    private Cookie3Repository crepo;
    @Autowired
    private User3Repository urepo;


    @BeforeEach
    void setUp() {
        urepo.deleteAll();
        urepo.flush();
        crepo.deleteAll();
        crepo.flush();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveSeparetellyWithManagedEntity() {
        Cookie3 goodOne = crepo.save(new Cookie3(null, "good"));
        Cookie3 badOne = crepo.save(new Cookie3(null, "bad"));
        crepo.flush();

        //you need to use the returned proxy instance to arrange relations with saved entities
        User3 fedor = urepo.save(new User3(null, "Fedor", null, null));
        fedor.setMyPrecious(Collections.singletonList(goodOne));
        fedor.setMyExPrecious(Collections.singletonList(badOne));
        fedor = urepo.save(fedor);
        urepo.flush();

        Optional<User3> fedorFromDB = urepo.findById(fedor.getId());
        assertTrue(fedorFromDB.isPresent());
        assertEquals(1,urepo.countByMyPrecious_Id(goodOne.getId()));
        assertEquals(1, urepo.countByMyExPrecious_Id(badOne.getId()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveSeparetellyForUnmanagedEntity() {
        Cookie3 goodOne = crepo.save(new Cookie3(null, "good"));
        Cookie3 badOne = crepo.save(new Cookie3(null, "bad"));
        crepo.flush();


        // you cannot use an unmanaged instance to arrange relations
        // with saved entities
        assertThrows(InvalidDataAccessApiUsageException.class, () ->
                urepo.save(new User3(null, "Fedor", Collections.singletonList(goodOne), null)));


        // but you can use an unmanaged instance to arrange relations
        // with saved entities if there is no cascading
        User3 kesha = urepo.save(new User3(null, "Enokentiy", null, Collections.singletonList(badOne)));
        urepo.flush();

        Optional<User3> keshaFromDB = urepo.findById(kesha.getId());
        assertTrue(keshaFromDB.isPresent());
        assertEquals(1, urepo.countByMyExPrecious_Id(badOne.getId()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveByRelationWithUnmanagedEntity() {
        Cookie3 goodOne = new Cookie3(null, "good");
        Cookie3 badOne = new Cookie3(null, "bad");

        // you can use an unmanaged instanceto arrange relations
        // with unsaved entities if a used cascadeType supports it
        User3 fedor = urepo.save(new User3(null, "Fedor", Collections.singletonList(goodOne), null));
        urepo.flush();
        crepo.flush();

        Optional<User3> fedorFromDB = urepo.findById(fedor.getId());
        Optional<Cookie3> goodOneFromDB = crepo.findFirstByTaste(goodOne.getTaste());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());

        // you cannot use an unmanaged instance to arrange relations
        // with unsaved entities if a used cascadeType doesn't support it
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> urepo.save(new User3(null, "Enokentiy", null, Collections.singletonList(badOne))));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanSaveByRelationWithManagedEntity() {
        Cookie3 goodOne = new Cookie3(null, "good");
        Cookie3 badOne = new Cookie3(null, "bad");

        User3 fedor = urepo.save(new User3(null, "Fedor", null, null));
        urepo.flush();

        // you can use a managed proxy instance to arrange relations
        // with unsaved entities if a used cascadeType supports it
        fedor.setMyPrecious(Collections.singletonList(goodOne));
        fedor = urepo.save(fedor);
        urepo.flush();
        crepo.flush();

        Optional<User3> fedorFromDB = urepo.findById(fedor.getId());
        Optional<Cookie3> goodOneFromDB = crepo.findFirstByTaste(goodOne.getTaste());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());

        // you cannot use a managed proxy instance to arrange relations
        // with unsaved entities if a used cascadeType doesn't support it
        fedor.setMyExPrecious(Collections.singletonList(badOne));
        User3 fedorForCheck = fedor;
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> urepo.save(fedorForCheck));

    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testCanReadUnderTransaction() {
        Cookie3 goodOne = crepo.save(new Cookie3(null, "good"));
        User3 fedor = urepo.save(new User3(null, "Fedor", Collections.singletonList(goodOne), null));

        Optional<User3> fedorFromDB = urepo.findById(fedor.getId());
        Optional<Cookie3> goodOneFromDB = crepo.findById(goodOne.getId());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());
        assertNotNull(fedorFromDB.get().getMyPrecious());
        assertEquals(1, fedorFromDB.get().getMyPrecious().size());
        assertEquals(goodOneFromDB.get(), fedorFromDB.get().getMyPrecious().get(0));

    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCannotReadWithoutTransaction() {
        Cookie3 goodOne = new Cookie3(null, "good");
        User3 fedor = urepo.save(new User3(null, "Fedor", Collections.singletonList(goodOne), null));

        Optional<User3> fedorFromDB = urepo.findById(fedor.getId());
        Optional<Cookie3> goodOneFromDB = crepo.findById(goodOne.getId());

        assertTrue(fedorFromDB.isPresent());
        assertTrue(goodOneFromDB.isPresent());
        assertThrows(LazyInitializationException.class, () -> fedorFromDB.get().getMyPrecious().size());
    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanDeleteData() {
        Cookie3 goodOne = new Cookie3(null, "good");
        Cookie3 badOne = new Cookie3(null, "bad");
        Cookie3 sosoOne = new Cookie3(null, "so-so");
        urepo.save(new User3(null, "Fedor", Collections.singletonList(goodOne), null));
        urepo.save(new User3(null, "Enokentiy", Collections.singletonList(badOne), null));
        urepo.save(new User3(null, "George", Collections.singletonList(sosoOne), null));
        crepo.flush();
        urepo.flush();

        urepo.deleteAll();
        crepo.deleteAll();
        crepo.flush();
        urepo.flush();

        assertEquals(0, crepo.count());
        assertEquals(0, urepo.count());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCannotDeleteSeparately() {
        Cookie3 goodOne = crepo.save(new Cookie3(null, "good"));
        Cookie3 badOne = crepo.save(new Cookie3(null, "bad"));
        User3 fedor = urepo.save(new User3(null, "Fedor", null, null));
        User3 kesha = urepo.save(new User3(null, "Enokentiy", null, null));
        fedor.setMyPrecious(Collections.singletonList(goodOne));
        kesha.setMyExPrecious(Collections.singletonList(badOne));
        urepo.save(fedor);
        urepo.save(kesha);
        crepo.flush();
        urepo.flush();

        // cannot delete a not-owner entity because of the foreign key constraints
        assertThrows(DataIntegrityViolationException.class,
                () -> crepo.delete(goodOne));
        assertThrows(DataIntegrityViolationException.class,
                () -> crepo.delete(badOne));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testCanDeleteByRelations() {
        Cookie3 goodOne = crepo.save(new Cookie3(null, "good"));
        Cookie3 badOne = crepo.save(new Cookie3(null, "bad"));
        User3 fedor = urepo.save(new User3(null, "Fedor", null, null));
        User3 kesha = urepo.save(new User3(null, "Enokentiy", null, null));
        fedor.setMyPrecious(Collections.singletonList(goodOne));
        kesha.setMyExPrecious(Collections.singletonList(badOne));
        urepo.save(fedor);
        urepo.save(kesha);
        crepo.flush();
        urepo.flush();

        // deletes related entity if it's supported by the used cascadeType
        urepo.delete(fedor);
        crepo.flush();
        urepo.flush();
        assertFalse(crepo.findById(goodOne.getId()).isPresent());
        assertFalse(urepo.findById(fedor.getId()).isPresent());

        // doesn't delete related entity if it isn't supported by the used cascadeType
        urepo.deleteById(kesha.getId());
        crepo.flush();
        urepo.flush();
        assertTrue(crepo.findById(badOne.getId()).isPresent());
        assertFalse(urepo.findById(kesha.getId()).isPresent());

    }


}