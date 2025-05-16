package io.sebi.buttonprototyping

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform