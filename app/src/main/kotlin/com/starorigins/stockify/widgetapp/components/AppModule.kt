package com.starorigins.stockify.widgetapp.components

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.starorigins.stockify.widgetapp.analytics.Analytics
import com.starorigins.stockify.widgetapp.analytics.GeneralProperties
import com.starorigins.stockify.widgetapp.components.AppClock.AppClockImpl
import com.starorigins.stockify.widgetapp.home.IAppReviewManager
import com.starorigins.stockify.widgetapp.repo.QuoteDao
import com.starorigins.stockify.widgetapp.repo.QuotesDB
import com.starorigins.stockify.widgetapp.repo.migrations.MIGRATION_1_2
import com.starorigins.stockify.widgetapp.repo.migrations.MIGRATION_2_3
import com.starorigins.stockify.widgetapp.repo.migrations.MIGRATION_3_4
import com.starorigins.stockify.widgetapp.repo.migrations.MIGRATION_4_5
import com.starorigins.stockify.widgetapp.repo.migrations.MIGRATION_5_6
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton
import com.starorigins.stockify.widgetapp.AppPreferences
import com.starorigins.stockify.widgetapp.analytics.AnalyticsImpl
import dagger.Lazy

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Singleton @Provides fun provideApplicationScope(): CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

  @Provides @Singleton fun provideClock(): AppClock = AppClockImpl

  @Provides @Singleton fun provideDefaultSharedPreferences(
    @ApplicationContext context: Context
  ): SharedPreferences =
    context.getSharedPreferences(AppPreferences.PREFS_NAME, MODE_PRIVATE)

  @Provides @Singleton fun provideAppWidgetManager(@ApplicationContext context: Context): AppWidgetManager =
    AppWidgetManager.getInstance(context)

  @Provides @Singleton fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
    WorkManager.getInstance(context)

  @Provides @Singleton fun provideAnalytics(
    @ApplicationContext context: Context,
    properties: dagger.Lazy<GeneralProperties>
  ): Analytics = AnalyticsImpl(context, properties)

    @Provides @Singleton fun provideFirebaseAnalytics(
        @ApplicationContext context: Context,
        properties: Lazy<GeneralProperties>
    ): String = FirebaseAnalytics.Event.SCREEN_VIEW

  @Provides @Singleton fun provideQuotesDB(@ApplicationContext context: Context): QuotesDB {
    return Room.databaseBuilder(
        context.applicationContext,
        QuotesDB::class.java, "quotes-db"
    )
        .addMigrations(MIGRATION_1_2)
        .addMigrations(MIGRATION_2_3)
        .addMigrations(MIGRATION_3_4)
        .addMigrations(MIGRATION_4_5)
        .addMigrations(MIGRATION_5_6)
        .build()
  }

  @Provides @Singleton fun provideQuoteDao(db: QuotesDB): QuoteDao = db.quoteDao()

  @Provides @Singleton fun providesAppReviewManager(@ApplicationContext context: Context): IAppReviewManager {
    return com.starorigins.stockify.widgetapp.home.AppReviewManager(context)
  }
}