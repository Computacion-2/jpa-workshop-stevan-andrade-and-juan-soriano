package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "period_summary",
        uniqueConstraints = @UniqueConstraint(name = "uk_ps_user_period",
                columnNames = {"user_id", "period_year", "period_month"}))
@Getter
@Setter
@NoArgsConstructor
public class PeriodSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "total_income", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "total_expense", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalExpense = BigDecimal.ZERO;

    @Column(name = "net_savings", nullable = false, precision = 14, scale = 2)
    private BigDecimal netSavings = BigDecimal.ZERO;

    @Column(name = "savings_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal savingsRate = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "periodSummary", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PeriodCategorySummary> categorySummaries = new HashSet<>();
}
