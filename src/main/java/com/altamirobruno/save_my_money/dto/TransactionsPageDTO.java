package com.altamirobruno.save_my_money.dto;

import java.util.List;

public record TransactionsPageDTO(List<TransactionDTO> transactions, int pageNumber, int pageSize, long totalElements, int totalPages) {
}
