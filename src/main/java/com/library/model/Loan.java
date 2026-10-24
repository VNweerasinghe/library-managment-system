package com.library.model;

import java.time.LocalDate;

public record Loan(
        long loanId,
        String memberId,
        String memberName,
        String bookId,
        String bookTitle,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate returnDate) {

    public String status() {
        if (returnDate != null) {
            return "Returned";
        }
        return dueDate.isBefore(LocalDate.now()) ? "Overdue" : "Borrowed";
    }

    @Override
    public String toString() {
        return bookTitle + " / " + memberName + " (due " + dueDate + ")";
    }
}
