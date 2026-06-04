package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BookDatabaseConstructor: RoomDatabaseConstructor<FavouriteBookDatabase> {
    override fun initialize(): FavouriteBookDatabase
}