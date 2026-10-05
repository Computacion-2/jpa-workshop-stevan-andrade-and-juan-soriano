package com.finanzas.repository;

import com.finanzas.entity.FinTransaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinTransactionRepository extends JpaRepository<FinTransaction, Long> {

    List<FinTransaction> findByAccountUserIdAndTransactionDateBetween(Long userId, LocalDate from, LocalDate to);
}
