package com.netah.hakkam.numyah.mind.extensions

inline fun <R> R?.otherwise(block: () -> R): R {
    return this ?: block()
}