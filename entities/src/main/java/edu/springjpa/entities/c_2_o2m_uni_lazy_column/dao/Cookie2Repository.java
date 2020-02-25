package edu.springjpa.entities.c_2_o2m_uni_lazy_column.dao;

import edu.springjpa.entities.c_2_o2m_uni_lazy_column.models.Cookie2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Cookie2Repository extends JpaRepository<Cookie2, Long> {

    long countByMyLord_Id(Long myLordId);
    long countByMyOldLord_Id(Long myOldLordId);
}
