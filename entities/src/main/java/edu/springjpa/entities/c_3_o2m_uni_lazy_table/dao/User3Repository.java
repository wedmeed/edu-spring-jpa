package edu.springjpa.entities.c_3_o2m_uni_lazy_table.dao;

import edu.springjpa.entities.c_3_o2m_uni_lazy_table.models.User3;
import org.springframework.data.jpa.repository.JpaRepository;

public interface User3Repository extends JpaRepository<User3, Long> {

    long countByMyPrecious_Id(Long myPreciousId);
    long countByMyExPrecious_Id(Long myExPreciousId);
}
