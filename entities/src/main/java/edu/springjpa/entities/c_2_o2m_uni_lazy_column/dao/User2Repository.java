package edu.springjpa.entities.c_2_o2m_uni_lazy_column.dao;

import edu.springjpa.entities.c_2_o2m_uni_lazy_column.models.User2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface User2Repository extends JpaRepository<User2, Long> {

    public Optional<User2> findFirstByName(String name);
}
