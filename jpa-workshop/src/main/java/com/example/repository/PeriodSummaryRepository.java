package com.example.repository;

import com.example.model.PeriodSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodSummaryRepository extends JpaRepository<PeriodSummary, Long> {

    List<PeriodSummary> findByUserIdOrderByPeriodYearDescPeriodMonthDesc(Long userId);
}
