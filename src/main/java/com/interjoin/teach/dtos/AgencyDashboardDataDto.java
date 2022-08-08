package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgencyDashboardDataDto {

    private BigDecimal availableBalance;
    private BigDecimal lastPayment;
    private Long totalHours;
    private BigDecimal totalAgencyEarnings;
}
