package com.library.service;

import com.library.data.LibraryRepository;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LibraryServiceTest {
    @TempDir
    Path temporaryDirectory;

    private LibraryService library;

    @BeforeEach
    void setUp() throws SQLException {
        library = new LibraryService(
                new LibraryRepository(temporaryDirectory.resolve("library.db")));
    }

    @Test
    void bookAndMemberRecordsAreSavedAndSearchable() throws SQLException {
        library.saveBook(new Book("B-001", "Clean Code", "Robert Martin", "Software",
                2008, 3, 0), false);
        library.saveMember(new Member("M-001", "Alex Reader", "alex@example.com",
                "0771234567", "Main Street"), false);

        assertEquals(1, library.books().size());
        assertEquals(3, library.books().get(0).availableQuantity());
        assertEquals("Alex Reader", library.members().get(0).fullName());
    }

    @Test
    void booksAndMembersCanBeUpdatedAndDeleted() throws SQLException {
        library.saveBook(new Book("B-001", "Clean Code", "Robert Martin", "Software",
                2008, 2, 0), false);
        library.saveMember(new Member("M-001", "Alex Reader", "alex@example.com",
                "0771234567", "Main Street"), false);

        library.saveBook(new Book("B-001", "Clean Code, Updated", "Robert Martin", "Software",
                2008, 4, 0), true);
        library.saveMember(new Member("M-001", "Alex R.", "alex.updated@example.com",
                "0777654321", "Oak Street"), true);

        assertEquals("Clean Code, Updated", library.books().get(0).title());
        assertEquals(4, library.books().get(0).availableQuantity());
        assertEquals("Alex R.", library.members().get(0).fullName());

        library.deleteBook("B-001");
        library.deleteMember("M-001");
        assertEquals(0, library.books().size());
        assertEquals(0, library.members().size());
    }

    @Test
    void issuingAndReturningBooksUpdatesAvailabilityAndHistory() throws SQLException {
        library.saveBook(new Book("B-001", "Clean Code", "Robert Martin", "Software",
                2008, 1, 0), false);
        library.saveMember(new Member("M-001", "Alex Reader", "alex@example.com",
                "0771234567", "Main Street"), false);
        LocalDate issueDate = LocalDate.now();

        library.issueBook("M-001", "B-001", issueDate, issueDate.plusDays(14));

        assertEquals(0, library.books().get(0).availableQuantity());
        assertEquals(1, library.activeLoans().size());
        Loan loan = library.activeLoans().get(0);

        library.returnBook(loan.loanId(), issueDate);

        assertEquals(1, library.books().get(0).availableQuantity());
        assertEquals(0, library.activeLoans().size());
        assertEquals("Returned", library.loans().get(0).status());
    }

    @Test
    void issuingUnavailableBookDoesNotCreateLoan() throws SQLException {
        library.saveBook(new Book("B-001", "Clean Code", "Robert Martin", "Software",
                2008, 1, 0), false);
        library.saveMember(new Member("M-001", "Alex Reader", "alex@example.com",
                "0771234567", "Main Street"), false);
        LocalDate today = LocalDate.now();
        library.issueBook("M-001", "B-001", today, today.plusDays(14));

        assertThrows(SQLException.class, () ->
                library.issueBook("M-001", "B-001", today, today.plusDays(14)));
        assertEquals(1, library.loans().size());
        assertEquals(0, library.books().get(0).availableQuantity());
    }

    @Test
    void bookQuantityCannotBeReducedBelowTheNumberCurrentlyOnLoan() throws SQLException {
        library.saveBook(new Book("B-001", "Clean Code", "Robert Martin", "Software",
                2008, 3, 0), false);
        library.saveMember(new Member("M-001", "Alex Reader", "alex@example.com",
                "0771234567", "Main Street"), false);
        library.saveMember(new Member("M-002", "Sam Reader", "sam@example.com",
                "0777654321", "Oak Street"), false);
        LocalDate today = LocalDate.now();
        library.issueBook("M-001", "B-001", today, today.plusDays(14));
        library.issueBook("M-002", "B-001", today, today.plusDays(14));

        assertThrows(SQLException.class, () -> library.saveBook(
                new Book("B-001", "Clean Code", "Robert Martin", "Software",
                        2008, 1, 1), true));
        assertEquals(1, library.books().get(0).availableQuantity());
        assertEquals(3, library.books().get(0).quantity());
    }

    @Test
    void borrowingHistoryPreventsDeletingReferencedBooksAndMembers() throws SQLException {
        library.saveBook(new Book("B-001", "Clean Code", "Robert Martin", "Software",
                2008, 1, 0), false);
        library.saveMember(new Member("M-001", "Alex Reader", "alex@example.com",
                "0771234567", "Main Street"), false);
        LocalDate today = LocalDate.now();
        library.issueBook("M-001", "B-001", today, today.plusDays(14));

        assertThrows(SQLException.class, () -> library.deleteBook("B-001"));
        assertThrows(SQLException.class, () -> library.deleteMember("M-001"));
        assertEquals(1, library.loans().size());
    }
}
