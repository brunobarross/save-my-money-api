package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.FinancialSummaryDTO;
import com.altamirobruno.save_my_money.dto.FinancialSummaryProjection;
import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FinancialService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    public FinancialService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public FinancialSummaryDTO getFinancialSummary(@Param("month") int month, @Param("year") int year, @AuthenticationPrincipal Jwt jwt) {
        UserDTO user = userService.getUserLogged(jwt);
        if(user == null){
            return null;
        }

        FinancialSummaryProjection financialProjection = transactionRepository.getFinancialSummary(month, year, user.id());
        return new FinancialSummaryDTO(financialProjection.getTotalReceipts(), financialProjection.getTotalExpenses(), financialProjection.getTotalBalance());
    }
}
