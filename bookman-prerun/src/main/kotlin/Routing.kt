package io.sebi

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*

fun Application.configureRouting() {
    // Get or create BookRepository
    val bookRepository = try {
        attributes[BookRepositoryKey]
    } catch (e: IllegalStateException) {
        // For tests, initialize storage and create repository
        StorageFactory.init()
        BookRepository().also { repo ->
            // Seed storage using runBlocking
            runBlocking {
                repo.seedStorage()
            }
        }
    }

    routing {
        // Home page - list all books
        get("/") {
            val books = bookRepository.getAllBooks()
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title("Bookman - Book Manager")
                    styleLink("/static/styles.css")
                }
                body {
                    h1 { +"Bookman - Book Manager" }

                    div("actions") {
                        a("/books/new") { +"Add New Book" }
                    }

                    if (books.isEmpty()) {
                        p { +"No books found. Add some books to get started." }
                    } else {
                        table {
                            thead {
                                tr {
                                    th { +"Title" }
                                    th { +"Author" }
                                    th { +"Year" }
                                    th { +"ISBN" }
                                    th { +"Actions" }
                                }
                            }
                            tbody {
                                books.forEach { book ->
                                    tr {
                                        td { +book.title }
                                        td { +book.author }
                                        td { +book.year.toString() }
                                        td { +book.isbn }
                                        td {
                                            a("/books/${book.id}") { +"View" }
                                            +" | "
                                            a("/books/${book.id}/edit") { +"Edit" }
                                            +" | "
                                            form(action = "/books/${book.id}/delete", method = FormMethod.post) {
                                                submitInput { value = "Delete" }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Book routes
        route("/books") {
            // Create new book form
            get("/new") {
                call.respondHtml {
                    head {
                        title("Add New Book")
                        styleLink("/static/styles.css")
                    }
                    body {
                        h1 { +"Add New Book" }

                        form(action = "/books", method = FormMethod.post) {
                            bookForm(BookForm())

                            div("form-group") {
                                submitInput { value = "Create Book" }
                                a("/") { +"Cancel" }
                            }
                        }
                    }
                }
            }

            // Create book (POST)
            post {
                val formParameters = call.receiveParameters()
                val bookForm = BookForm(
                    title = formParameters["title"] ?: "",
                    author = formParameters["author"] ?: "",
                    year = formParameters["year"]?.toIntOrNull() ?: 0,
                    isbn = formParameters["isbn"] ?: "",
                    description = formParameters["description"] ?: ""
                )

                val book = bookRepository.addBook(bookForm.toBook())
                call.respondRedirect("/books/${book.id}")
            }

            // View book details
            get("/{id}") {
                val id = call.parameters.getOrFail<Int>("id")
                val book = bookRepository.getBook(id)

                if (book != null) {
                    call.respondHtml {
                        head {
                            title("Book Details: ${book.title}")
                            styleLink("/static/styles.css")
                        }
                        body {
                            h1 { +"Book Details" }

                            div("book-details") {
                                h2 { +book.title }
                                p { +"Author: ${book.author}" }
                                p { +"Year: ${book.year}" }
                                p { +"ISBN: ${book.isbn}" }
                                p { +"Description: ${book.description}" }
                            }

                            div("actions") {
                                a("/") { +"Back to List" }
                                +" | "
                                a("/books/${book.id}/edit") { +"Edit" }
                                +" | "
                                form(action = "/books/${book.id}/delete", method = FormMethod.post, classes = "inline") {
                                    submitInput { value = "Delete" }
                                }
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            // Edit book form
            get("/{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id")
                val book = bookRepository.getBook(id)

                if (book != null) {
                    val bookForm = BookForm(
                        title = book.title,
                        author = book.author,
                        year = book.year,
                        isbn = book.isbn,
                        description = book.description
                    )

                    call.respondHtml {
                        head {
                            title("Edit Book: ${book.title}")
                            styleLink("/static/styles.css")
                        }
                        body {
                            h1 { +"Edit Book" }

                            form(action = "/books/${book.id}", method = FormMethod.post) {
                                bookForm(bookForm)

                                div("form-group") {
                                    submitInput { value = "Update Book" }
                                    a("/books/${book.id}") { +"Cancel" }
                                }
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            // Update book (POST)
            post("/{id}") {
                val id = call.parameters.getOrFail<Int>("id")
                val formParameters = call.receiveParameters()

                val bookForm = BookForm(
                    title = formParameters["title"] ?: "",
                    author = formParameters["author"] ?: "",
                    year = formParameters["year"]?.toIntOrNull() ?: 0,
                    isbn = formParameters["isbn"] ?: "",
                    description = formParameters["description"] ?: ""
                )

                val updated = bookRepository.updateBook(id, bookForm.toBook())
                if (updated) {
                    call.respondRedirect("/books/$id")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            // Delete book
            post("/{id}/delete") {
                val id = call.parameters.getOrFail<Int>("id")
                val deleted = bookRepository.deleteBook(id)

                if (deleted) {
                    call.respondRedirect("/")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }
        }

        // API routes for JSON
        route("/api/books") {
            get {
                call.respond(bookRepository.getAllBooks())
            }

            get("/{id}") {
                val id = call.parameters.getOrFail<Int>("id")
                val book = bookRepository.getBook(id)

                if (book != null) {
                    call.respond(book)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            post {
                val book = call.receive<Book>()
                call.respond(HttpStatusCode.Created, bookRepository.addBook(book))
            }

            put("/{id}") {
                val id = call.parameters.getOrFail<Int>("id")
                val book = call.receive<Book>()

                val updated = bookRepository.updateBook(id, book)
                if (updated) {
                    call.respond(HttpStatusCode.OK, bookRepository.getBook(id)!!)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            delete("/{id}") {
                val id = call.parameters.getOrFail<Int>("id")
                val deleted = bookRepository.deleteBook(id)

                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }
        }

        // Static resources
        staticResources("/static", "static")
    }
}

// Helper function for book form
private fun FlowContent.bookForm(book: BookForm) {
    div("form-group") {
        label { +"Title" }
        textInput(name = "title") {
            required = true
            value = book.title
        }
    }

    div("form-group") {
        label { +"Author" }
        textInput(name = "author") {
            required = true
            value = book.author
        }
    }

    div("form-group") {
        label { +"Year" }
        numberInput(name = "year") {
            required = true
            value = book.year.toString()
            min = "1000"
            max = "2100"
        }
    }

    div("form-group") {
        label { +"ISBN" }
        textInput(name = "isbn") {
            required = true
            value = book.isbn
        }
    }

    div("form-group") {
        label { +"Description" }
        textArea(rows = "5") {
            name = "description"
            +book.description
        }
    }
}
