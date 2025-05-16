package io.sebi

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.*

class ApplicationTest {

    // Helper function to generate a random ISBN
    private fun generateRandomISBN(): String {
        return (1..13).joinToString("") { Random.nextInt(0, 10).toString() }
    }

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testGetAllBooks() = testApplication {
        application {
            module()
        }

        client.get("/api/books").apply {
            assertEquals(HttpStatusCode.OK, status)
            val responseBody = body<String>()
            assertTrue(responseBody.contains("title"))
            assertTrue(responseBody.contains("author"))
        }
    }

    @Test
    fun testCreateAndGetBook() = testApplication {
        application {
            module()
        }

        // Create a new book with random ISBN
        val randomISBN = generateRandomISBN()
        val createResponse = client.post("/api/books") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "title": "Test Book",
                    "author": "Test Author",
                    "year": 2023,
                    "isbn": "$randomISBN",
                    "description": "A test book for unit testing"
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.Created, createResponse.status)
        val responseText = createResponse.body<String>()
        val createdBook = Json.decodeFromString<Book>(responseText)
        assertNotEquals(0, createdBook.id)
        assertEquals("Test Book", createdBook.title)

        // Get the created book
        val getResponse = client.get("/api/books/${createdBook.id}")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val retrievedBookText = getResponse.body<String>()
        val retrievedBook = Json.decodeFromString<Book>(retrievedBookText)
        assertEquals(createdBook.id, retrievedBook.id)
        assertEquals("Test Book", retrievedBook.title)
        assertEquals("Test Author", retrievedBook.author)
    }

    @Test
    fun testUpdateBook() = testApplication {
        application {
            module()
        }

        // Create a book to update with random ISBN
        val randomISBN = generateRandomISBN()
        val createResponse = client.post("/api/books") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "title": "Original Title",
                    "author": "Original Author",
                    "year": 2020,
                    "isbn": "$randomISBN",
                    "description": "Original description"
                }
            """.trimIndent())
        }

        val responseText = createResponse.body<String>()
        val createdBook = Json.decodeFromString<Book>(responseText)

        // Update the book with a new random ISBN
        val newRandomISBN = generateRandomISBN()
        val updateResponse = client.put("/api/books/${createdBook.id}") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "title": "Updated Title",
                    "author": "Updated Author",
                    "year": 2021,
                    "isbn": "$newRandomISBN",
                    "description": "Updated description"
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.OK, updateResponse.status)
        val updatedBookText = updateResponse.body<String>()
        val updatedBook = Json.decodeFromString<Book>(updatedBookText)
        assertEquals(createdBook.id, updatedBook.id)
        assertEquals("Updated Title", updatedBook.title)
        assertEquals("Updated Author", updatedBook.author)

        // Verify the update
        val getResponse = client.get("/api/books/${createdBook.id}")
        val retrievedBookText = getResponse.body<String>()
        val retrievedBook = Json.decodeFromString<Book>(retrievedBookText)
        assertEquals("Updated Title", retrievedBook.title)
    }

    @Test
    fun testDeleteBook() = testApplication {
        application {
            module()
        }

        // Create a book to delete with random ISBN
        val randomISBN = generateRandomISBN()
        val createResponse = client.post("/api/books") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "title": "Book to Delete",
                    "author": "Delete Author",
                    "year": 2022,
                    "isbn": "$randomISBN",
                    "description": "This book will be deleted"
                }
            """.trimIndent())
        }

        val responseText = createResponse.body<String>()
        val createdBook = Json.decodeFromString<Book>(responseText)

        // Delete the book
        val deleteResponse = client.delete("/api/books/${createdBook.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // Verify the book is deleted
        val getResponse = client.get("/api/books/${createdBook.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun testFormSubmission() = testApplication {
        application {
            module()
        }

        // Test form submission for creating a book with random ISBN
        val randomISBN = generateRandomISBN()
        val formResponse = client.submitForm(
            url = "/books",
            formParameters = parameters {
                append("title", "Form Test Book")
                append("author", "Form Test Author")
                append("year", "2023")
                append("isbn", randomISBN)
                append("description", "A book created via form submission")
            }
        )

        // Should redirect to the book details page
        assertEquals(HttpStatusCode.Found, formResponse.status)

        // Get all books and check if our book is there
        val allBooksResponse = client.get("/api/books")
        val responseBody = allBooksResponse.body<String>()
        assertTrue(responseBody.contains("Form Test Book"))
        assertTrue(responseBody.contains("Form Test Author"))
    }
}
