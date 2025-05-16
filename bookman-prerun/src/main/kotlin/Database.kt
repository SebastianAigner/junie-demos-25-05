package io.sebi

import io.ktor.server.application.*
import io.ktor.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

// Application attribute key for BookRepository
val BookRepositoryKey = AttributeKey<BookRepository>("BookRepository")

object StorageFactory {
    fun init() {
        // No initialization needed for in-memory storage
    }
}

class BookRepository {
    // Use ConcurrentHashMap for thread safety
    private val books = ConcurrentHashMap<Int, Book>()
    // Use AtomicInteger for thread-safe ID generation
    private val idCounter = AtomicInteger(0)

    suspend fun getAllBooks(): List<Book> {
        return books.values.toList()
    }

    suspend fun getBook(id: Int): Book? {
        return books[id]
    }

    suspend fun addBook(book: Book): Book {
        val id = idCounter.incrementAndGet()
        val newBook = book.copy(id = id)
        books[id] = newBook
        return newBook
    }

    suspend fun updateBook(id: Int, book: Book): Boolean {
        if (books.containsKey(id)) {
            books[id] = book.copy(id = id)
            return true
        }
        return false
    }

    suspend fun deleteBook(id: Int): Boolean {
        return books.remove(id) != null
    }

    suspend fun seedStorage() {
        // Only seed if the storage is empty
        if (books.isEmpty()) {
            val sampleBooks = listOf(
                Book(title = "To Kill a Mockingbird", author = "Harper Lee", year = 1960, isbn = "9780061120084", description = "A classic of modern American literature"),
                Book(title = "1984", author = "George Orwell", year = 1949, isbn = "9780451524935", description = "A dystopian social science fiction novel"),
                Book(title = "The Great Gatsby", author = "F. Scott Fitzgerald", year = 1925, isbn = "9780743273565", description = "A novel about the American Dream"),
                Book(title = "Pride and Prejudice", author = "Jane Austen", year = 1813, isbn = "9780141439518", description = "A romantic novel of manners"),
                Book(title = "The Catcher in the Rye", author = "J.D. Salinger", year = 1951, isbn = "9780316769488", description = "A novel about teenage angst and alienation"),
                Book(title = "The Hobbit", author = "J.R.R. Tolkien", year = 1937, isbn = "9780547928227", description = "A fantasy novel and children's book"),
                Book(title = "Brave New World", author = "Aldous Huxley", year = 1932, isbn = "9780060850524", description = "A dystopian social science fiction novel"),
                Book(title = "The Lord of the Rings", author = "J.R.R. Tolkien", year = 1954, isbn = "9780618640157", description = "An epic high-fantasy novel"),
                Book(title = "Animal Farm", author = "George Orwell", year = 1945, isbn = "9780451526342", description = "An allegorical novella"),
                Book(title = "The Grapes of Wrath", author = "John Steinbeck", year = 1939, isbn = "9780143039433", description = "A realist novel")
            )

            sampleBooks.forEach { book ->
                addBook(book)
            }
        }
    }
}
