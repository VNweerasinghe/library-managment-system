package com.library.data;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryRepository {
    private final String jdbcUrl;

    public LibraryRepository(Path databasePath) throws SQLException {
        try {
            Path absolutePath = databasePath.toAbsolutePath();
            Files.createDirectories(absolutePath.getParent());
            jdbcUrl = "jdbc:sqlite:" + absolutePath;
        } catch (IOException | SecurityException exception) {
            throw new SQLException("Could not prepare the library database location.", exception);
        }
        initialize();
    }

    private Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void initialize() throws SQLException {
        String[] statements = {
                """
                CREATE TABLE IF NOT EXISTS books (
                    book_id TEXT PRIMARY KEY COLLATE NOCASE,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    category TEXT NOT NULL,
                    published_year INTEGER NOT NULL CHECK (published_year BETWEEN 0 AND 9999),
                    quantity INTEGER NOT NULL CHECK (quantity >= 1),
                    available_quantity INTEGER NOT NULL CHECK (
                        available_quantity >= 0 AND available_quantity <= quantity
                    )
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS members (
                    member_id TEXT PRIMARY KEY COLLATE NOCASE,
                    full_name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    address TEXT NOT NULL
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS loans (
                    loan_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_id TEXT NOT NULL REFERENCES members(member_id) ON DELETE RESTRICT,
                    book_id TEXT NOT NULL REFERENCES books(book_id) ON DELETE RESTRICT,
                    issue_date TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    return_date TEXT,
                    CHECK (due_date >= issue_date),
                    CHECK (return_date IS NULL OR return_date >= issue_date)
                )
                """,
                "CREATE INDEX IF NOT EXISTS idx_loans_return_date ON loans(return_date)",
                "CREATE INDEX IF NOT EXISTS idx_loans_member ON loans(member_id)",
                "CREATE INDEX IF NOT EXISTS idx_loans_book ON loans(book_id)"
        };
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }

    public List<Book> findBooks() throws SQLException {
        String sql = "SELECT book_id, title, author, category, published_year, quantity, available_quantity "
                + "FROM books ORDER BY title COLLATE NOCASE";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {
            List<Book> books = new ArrayList<>();
            while (rows.next()) {
                books.add(new Book(rows.getString("book_id"), rows.getString("title"),
                        rows.getString("author"), rows.getString("category"),
                        rows.getInt("published_year"), rows.getInt("quantity"),
                        rows.getInt("available_quantity")));
            }
            return books;
        }
    }

    public void createBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (book_id, title, author, category, published_year, quantity, "
                + "available_quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.bookId());
            statement.setString(2, book.title());
            statement.setString(3, book.author());
            statement.setString(4, book.category());
            statement.setInt(5, book.publishedYear());
            statement.setInt(6, book.quantity());
            statement.setInt(7, book.quantity());
            statement.executeUpdate();
        }
    }

    public void updateBook(Book book) throws SQLException {
        String sql = """
                UPDATE books
                SET title = ?, author = ?, category = ?, published_year = ?,
                    available_quantity = available_quantity + (? - quantity), quantity = ?
                WHERE book_id = ? AND ? >= quantity - available_quantity
                """;
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.title());
            statement.setString(2, book.author());
            statement.setString(3, book.category());
            statement.setInt(4, book.publishedYear());
            statement.setInt(5, book.quantity());
            statement.setInt(6, book.quantity());
            statement.setString(7, book.bookId());
            statement.setInt(8, book.quantity());
            if (statement.executeUpdate() == 0) {
                throw new SQLException("The book does not exist or its quantity is below the number on loan.");
            }
        }
    }

    public void deleteBook(String bookId) throws SQLException {
        deleteById("DELETE FROM books WHERE book_id = ?", bookId, "book", "book");
    }

    public List<Member> findMembers() throws SQLException {
        String sql = "SELECT member_id, full_name, email, phone, address FROM members ORDER BY full_name COLLATE NOCASE";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {
            List<Member> members = new ArrayList<>();
            while (rows.next()) {
                members.add(new Member(rows.getString("member_id"), rows.getString("full_name"),
                        rows.getString("email"), rows.getString("phone"), rows.getString("address")));
            }
            return members;
        }
    }

    public void createMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (member_id, full_name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bindMember(statement, member);
            statement.executeUpdate();
        }
    }

    public void updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET full_name = ?, email = ?, phone = ?, address = ? WHERE member_id = ?";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, member.fullName());
            statement.setString(2, member.email());
            statement.setString(3, member.phone());
            statement.setString(4, member.address());
            statement.setString(5, member.memberId());
            if (statement.executeUpdate() == 0) {
                throw new SQLException("The member no longer exists.");
            }
        }
    }

    private void bindMember(PreparedStatement statement, Member member) throws SQLException {
        statement.setString(1, member.memberId());
        statement.setString(2, member.fullName());
        statement.setString(3, member.email());
        statement.setString(4, member.phone());
        statement.setString(5, member.address());
    }

    public void deleteMember(String memberId) throws SQLException {
        deleteById("DELETE FROM members WHERE member_id = ?", memberId, "member", "member");
    }

    private void deleteById(String sql, String id, String entity, String referenceName) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("The " + entity + " no longer exists.");
            }
        } catch (SQLException exception) {
            if (String.valueOf(exception.getMessage()).contains("FOREIGN KEY constraint failed")) {
                throw new SQLException("This " + referenceName
                        + " has borrowing history and cannot be deleted.", exception);
            }
            throw exception;
        }
    }

    public List<Loan> findLoans() throws SQLException {
        String sql = """
                SELECT l.loan_id, l.member_id, m.full_name, l.book_id, b.title,
                       l.issue_date, l.due_date, l.return_date
                FROM loans l
                JOIN members m ON m.member_id = l.member_id
                JOIN books b ON b.book_id = l.book_id
                ORDER BY l.issue_date DESC, l.loan_id DESC
                """;
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(sql)) {
            List<Loan> loans = new ArrayList<>();
            while (rows.next()) {
                String returned = rows.getString("return_date");
                loans.add(new Loan(rows.getLong("loan_id"), rows.getString("member_id"),
                        rows.getString("full_name"), rows.getString("book_id"), rows.getString("title"),
                        LocalDate.parse(rows.getString("issue_date")),
                        LocalDate.parse(rows.getString("due_date")),
                        returned == null ? null : LocalDate.parse(returned)));
            }
            return loans;
        }
    }

    public List<Loan> findActiveLoans() throws SQLException {
        return findLoans().stream().filter(loan -> loan.returnDate() == null).toList();
    }

    public void issueBook(String memberId, String bookId, LocalDate issueDate, LocalDate dueDate)
            throws SQLException {
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE books SET available_quantity = available_quantity - 1 "
                                + "WHERE book_id = ? AND available_quantity > 0")) {
                    update.setString(1, bookId);
                    if (update.executeUpdate() == 0) {
                        throw new SQLException("The selected book is unavailable.");
                    }
                }
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO loans (member_id, book_id, issue_date, due_date) VALUES (?, ?, ?, ?)")) {
                    insert.setString(1, memberId);
                    insert.setString(2, bookId);
                    insert.setString(3, issueDate.toString());
                    insert.setString(4, dueDate.toString());
                    insert.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public void returnBook(long loanId, LocalDate returnDate) throws SQLException {
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                String bookId;
                try (PreparedStatement query = connection.prepareStatement(
                        "SELECT book_id, issue_date FROM loans WHERE loan_id = ? AND return_date IS NULL")) {
                    query.setLong(1, loanId);
                    try (ResultSet row = query.executeQuery()) {
                        if (!row.next()) {
                            throw new SQLException("The selected loan is already returned or no longer exists.");
                        }
                        if (returnDate.isBefore(LocalDate.parse(row.getString("issue_date")))) {
                            throw new SQLException("The return date cannot be before the issue date.");
                        }
                        bookId = row.getString("book_id");
                    }
                }
                try (PreparedStatement updateLoan = connection.prepareStatement(
                        "UPDATE loans SET return_date = ? WHERE loan_id = ? AND return_date IS NULL")) {
                    updateLoan.setString(1, returnDate.toString());
                    updateLoan.setLong(2, loanId);
                    if (updateLoan.executeUpdate() == 0) {
                        throw new SQLException("The selected loan was already returned.");
                    }
                }
                try (PreparedStatement updateBook = connection.prepareStatement(
                        "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id = ?")) {
                    updateBook.setString(1, bookId);
                    updateBook.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public long countBooks() throws SQLException {
        return count("SELECT COUNT(*) FROM books");
    }

    public long countMembers() throws SQLException {
        return count("SELECT COUNT(*) FROM members");
    }

    public long countActiveLoans() throws SQLException {
        return count("SELECT COUNT(*) FROM loans WHERE return_date IS NULL");
    }

    public long countOverdueLoans() throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM loans WHERE return_date IS NULL AND due_date < ?")) {
            statement.setString(1, LocalDate.now().toString());
            try (ResultSet row = statement.executeQuery()) {
                return row.next() ? row.getLong(1) : 0;
            }
        }
    }

    private long count(String sql) throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet row = statement.executeQuery(sql)) {
            return row.next() ? row.getLong(1) : 0;
        }
    }
}
