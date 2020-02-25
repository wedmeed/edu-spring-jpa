package edu.springjpa.entities.c_3_o2m_uni_lazy_table.dao;

import edu.springjpa.entities.c_3_o2m_uni_lazy_table.models.Cookie3;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Cookie3Repository extends JpaRepository<Cookie3, Long> {
    Optional<Cookie3> findFirstByTaste(String taste);
}
