package com.example.repository;

import com.example.model.PeriodCategorySummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodCategorySummaryRepository extends JpaRepository<PeriodCategorySummary, Long> {

    List<PeriodCategorySummary> findByPeriodSummaryId(Long periodSummaryId);
}
