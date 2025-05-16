package io.sebi

import io.ktor.server.application.*
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Initialize storage
    StorageFactory.init()

    // Create repository
    val bookRepository = BookRepository()

    // Seed storage with sample books
    environment.monitor.subscribe(ApplicationStarted) {
        launch {
            bookRepository.seedStorage()
        }
    }

    configureSerialization()
    configureRouting()

    // Store repository in application attributes for access in routes
    attributes.put(BookRepositoryKey, bookRepository)
}
