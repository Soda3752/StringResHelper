package org.srtingres.helper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform