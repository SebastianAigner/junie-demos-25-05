package io.sebi

import kotlinx.serialization.Serializable

// Serializable data class for API responses and requests
@Serializable
data class Book(
    val id: Int = 0,
    val title: String,
    val author: String,
    val year: Int,
    val isbn: String,
    val description: String
)

// Data class for book creation form
data class BookForm(
    val title: String = "",
    val author: String = "",
    val year: Int = 0,
    val isbn: String = "",
    val description: String = ""
) {
    fun toBook() = Book(
        title = title,
        author = author,
        year = year,
        isbn = isbn,
        description = description
    )
}
