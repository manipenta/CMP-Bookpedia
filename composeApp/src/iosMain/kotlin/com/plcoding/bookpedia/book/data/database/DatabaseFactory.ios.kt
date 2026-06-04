package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabase

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<FavouriteBookDatabase> {
        val dbFile = documentDirectory()
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory.path)
        )
    }
}