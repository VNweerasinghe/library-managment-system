package com.library.model;

public record Book(
        String bookId,
        String title,
        String author,
        String category,
        int publishedYear,
        int quantity,
        int availableQuantity) {

    @Override
    public String toString() {
        return bookId + " - " + title + " (" + availableQuantity + " available)";
    }
}
