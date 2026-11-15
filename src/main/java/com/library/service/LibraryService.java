package com.library.service;

import com.library.data.LibraryRepository;
import com.library.dto.BookDto;
import com.library.dto.LoanDto;
import com.library.dto.MemberDto;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LibraryService {
    private final LibraryRepository repository;

    public LibraryService(LibraryRepository repository) {
        this.repository = repository;
    }

    public List<BookDto> books() throws SQLException {
        return repository.findBooks().stream().map(LibraryService::toBookDto).toList();
    }

    public void saveBook(BookDto book, boolean editing) throws SQLException {
        requireText(book.bookId(), "Book ID");
        requireText(book.title(), "Title");
        requireText(book.author(), "Author");
        requireText(book.category(), "Category");
        if (book.publishedYear() < 0 || book.publishedYear() > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Enter a valid published year.");
        }
        if (book.quantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least one.");
        }
        if (editing) {
            repository.updateBook(toBook(book));
        } else {
            repository.createBook(toBook(book));
        }
    }

    public void deleteBook(String bookId) throws SQLException {
        repository.deleteBook(bookId);
    }

    public List<MemberDto> members() throws SQLException {
        return repository.findMembers().stream().map(LibraryService::toMemberDto).toList();
    }

    public void saveMember(MemberDto member, boolean editing) throws SQLException {
        requireText(member.memberId(), "Member ID");
        requireText(member.fullName(), "Full name");
        requireText(member.email(), "Email");
        requireText(member.phone(), "Phone number");
        requireText(member.address(), "Address");
        if (!member.email().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (editing) {
            repository.updateMember(toMember(member));
        } else {
            repository.createMember(toMember(member));
        }
    }

    public void deleteMember(String memberId) throws SQLException {
        repository.deleteMember(memberId);
    }

    public List<LoanDto> loans() throws SQLException {
        return repository.findLoans().stream().map(LibraryService::toLoanDto).toList();
    }

    public List<LoanDto> activeLoans() throws SQLException {
        return repository.findActiveLoans().stream().map(LibraryService::toLoanDto).toList();
    }

    public void issueBook(String memberId, String bookId, LocalDate issueDate, LocalDate dueDate)
            throws SQLException {
        if (memberId == null || memberId.isBlank() || bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Select a member and a book.");
        }
        if (issueDate == null || dueDate == null || dueDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Choose valid issue and due dates.");
        }
        if (issueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("An issue date cannot be in the future.");
        }
        repository.issueBook(memberId, bookId, issueDate, dueDate);
    }

    public void returnBook(long loanId, LocalDate returnDate) throws SQLException {
        if (returnDate == null) {
            throw new IllegalArgumentException("Choose a return date.");
        }
        if (returnDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A return date cannot be in the future.");
        }
        repository.returnBook(loanId, returnDate);
    }

    public long totalBooks() throws SQLException {
        return repository.countBooks();
    }

    public long totalMembers() throws SQLException {
        return repository.countMembers();
    }

    public long borrowedBooks() throws SQLException {
        return repository.countActiveLoans();
    }

    public long overdueBooks() throws SQLException {
        return repository.countOverdueLoans();
    }

    private void requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private static BookDto toBookDto(Book book) {
        return new BookDto(book.bookId(), book.title(), book.author(), book.category(),
                book.publishedYear(), book.quantity(), book.availableQuantity());
    }

    private static Book toBook(BookDto book) {
        return new Book(book.bookId(), book.title(), book.author(), book.category(),
                book.publishedYear(), book.quantity(), book.availableQuantity());
    }

    private static MemberDto toMemberDto(Member member) {
        return new MemberDto(member.memberId(), member.fullName(), member.email(),
                member.phone(), member.address());
    }

    private static Member toMember(MemberDto member) {
        return new Member(member.memberId(), member.fullName(), member.email(),
                member.phone(), member.address());
    }

    private static LoanDto toLoanDto(Loan loan) {
        return new LoanDto(loan.loanId(), loan.memberId(), loan.memberName(), loan.bookId(),
                loan.bookTitle(), loan.issueDate(), loan.dueDate(), loan.returnDate());
    }
}
