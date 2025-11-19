import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Book implements Serializable, Comparable<Book> {
    private Integer bookId;
    private String title;
    private String author;
    private String category;
    private Boolean isIssued;

    public Book(Integer bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = false;
    }

    public Integer getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public Boolean getIsIssued() {
        return isIssued;
    }

    public void displayBookDetails() {
        System.out.println("ID: " + bookId + ", Title: " + title + ", Author: " + author + ", Category: " + category + ", Issued: " + isIssued);
    }

    public void markAsIssued() {
        this.isIssued = true;
    }

    public void markAsReturned() {
        this.isIssued = false;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareTo(other.title);
    }
}

class Member implements Serializable {
    private Integer memberId;
    private String name;
    private String email;
    private List<Integer> issuedBooks;

    public Member(Integer memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.issuedBooks = new ArrayList<>();
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public void displayMemberDetails() {
        System.out.println("ID: " + memberId + ", Name: " + name + ", Email: " + email + ", Issued Books: " + issuedBooks.size());
    }

    public void addIssuedBook(int bookId) {
        issuedBooks.add(bookId);
    }

    public void returnIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }

    public boolean hasIssuedBook(int bookId) {
        return issuedBooks.contains(bookId);
    }
}

class LibraryManager {
    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, Member> members = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private final String BOOKS_FILE = "books.txt";
    private final String MEMBERS_FILE = "members.txt";

    public LibraryManager() {
        loadFromFile();
    }

    public void loadFromFile() {
        try {
            if (Files.exists(Paths.get(BOOKS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKS_FILE))) {
                    books = (Map<Integer, Book>) ois.readObject();
                }
            }
            if (Files.exists(Paths.get(MEMBERS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MEMBERS_FILE))) {
                    members = (Map<Integer, Member>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data from files: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKS_FILE))) {
                oos.writeObject(books);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MEMBERS_FILE))) {
                oos.writeObject(members);
            }
        } catch (IOException e) {
            System.out.println("Error saving data to files: " + e.getMessage());
        }
    }

    public void addBook() {
        try {
            System.out.print("Enter Book Title: ");
            String title = scanner.nextLine();
            System.out.print("Enter Author: ");
            String author = scanner.nextLine();
            System.out.print("Enter Category: ");
            String category = scanner.nextLine();

            int newId = books.isEmpty() ? 101 : Collections.max(books.keySet()) + 1;
            Book newBook = new Book(newId, title, author, category);
            books.put(newId, newBook);
            saveToFile();
            System.out.println("Book added successfully with ID: " + newId);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void addMember() {
        try {
            System.out.print("Enter Member Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();

            int newId = members.isEmpty() ? 201 : Collections.max(members.keySet()) + 1;
            Member newMember = new Member(newId, name, email);
            members.put(newId, newMember);
            saveToFile();
            System.out.println("Member added successfully with ID: " + newId);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void issueBook() {
        try {
            System.out.print("Enter Book ID to issue: ");
            int bookId = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            Book book = books.get(bookId);
            Member member = members.get(memberId);

            if (book == null) {
                System.out.println("Error: Book ID not found.");
                return;
            }
            if (member == null) {
                System.out.println("Error: Member ID not found.");
                return;
            }
            if (book.getIsIssued()) {
                System.out.println("Error: Book is already issued.");
                return;
            }

            book.markAsIssued();
            member.addIssuedBook(bookId);
            saveToFile();
            System.out.println("Book " + bookId + " issued to Member " + memberId + " successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID format.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void returnBook() {
        try {
            System.out.print("Enter Book ID to return: ");
            int bookId = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            Book book = books.get(bookId);
            Member member = members.get(memberId);

            if (book == null) {
                System.out.println("Error: Book ID not found.");
                return;
            }
            if (member == null) {
                System.out.println("Error: Member ID not found.");
                return;
            }
            if (!member.hasIssuedBook(bookId)) {
                System.out.println("Error: This book was not issued to this member.");
                return;
            }

            book.markAsReturned();
            member.returnIssuedBook(bookId);
            saveToFile();
            System.out.println("Book " + bookId + " returned successfully by Member " + memberId + ".");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID format.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void searchBooks() {
        System.out.print("Enter search term (title, author, or category): ");
        String term = scanner.nextLine().toLowerCase();
        boolean found = false;
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(term) || book.getAuthor().toLowerCase().contains(term) || book.getCategory().toLowerCase().contains(term)) {
                book.displayBookDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found matching the search term.");
        }
    }

    public void sortBooks() {
        List<Book> bookList = new ArrayList<>(books.values());
        System.out.println("Sort by: 1. Title (default) 2. Author 3. Category");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("2")) {
            bookList.sort(Comparator.comparing(Book::getAuthor));
        } else if (choice.equals("3")) {
            bookList.sort(Comparator.comparing(Book::getCategory));
        } else {
            Collections.sort(bookList);
        }

        for (Book book : bookList) {
            book.displayBookDetails();
        }
    }

    public void mainMenu() {
        int choice = 0;
        while (choice != 7) {
            System.out.println("\nWelcome to City Library Digital Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        addMember();
                        break;
                    case 3:
                        issueBook();
                        break;
                    case 4:
                        returnBook();
                        break;
                    case 5:
                        searchBooks();
                        break;
                    case 6:
                        sortBooks();
                        break;
                    case 7:
                        saveToFile();
                        System.out.println("Exiting system. Data saved.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = 0;
            }
        }
        scanner.close();
    }
}

public class CityLibrarySystem {
    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();
        manager.mainMenu();
    }
}
