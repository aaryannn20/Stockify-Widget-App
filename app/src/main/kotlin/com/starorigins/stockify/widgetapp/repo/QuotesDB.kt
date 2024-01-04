package com.starorigins.stockify.widgetapp.repo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.starorigins.stockify.widgetapp.repo.data.HoldingRow
import com.starorigins.stockify.widgetapp.repo.data.PropertiesRow
import com.starorigins.stockify.widgetapp.repo.data.QuoteRow

@Database(
    entities = [QuoteRow::class, HoldingRow::class, PropertiesRow::class],
    version = 6,
    exportSchema = true
)
abstract class QuotesDB : RoomDatabase() {
  abstract fun quoteDao(): QuoteDao
}