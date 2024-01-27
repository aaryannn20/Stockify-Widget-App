package com.starorigins.stockify.widgetapp.network

import android.content.Context
import com.starorigins.stockify.widgetapp.BuildConfig
import com.starorigins.stockify.widgetapp.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

  companion object {
    internal const val CONNECTION_TIMEOUT: Long = 5000
    internal const val READ_TIMEOUT: Long = 5000
  }

  @Provides @Singleton internal fun provideHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
  ): OkHttpClient {
    val logger = HttpLoggingInterceptor()
    logger.level =
      if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    val okHttpClient =
      OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .addInterceptor(logger)
        .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .build()
    return okHttpClient
  }

  @Named("yahoo")
  @Provides @Singleton internal fun provideHttpClientForYahoo(
    crumbInterceptor: CrumbInterceptor,
    cookieJar: YahooFinanceCookies
  ): OkHttpClient {
    val logger = HttpLoggingInterceptor()
    logger.level =
      if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    val okHttpClient =
      OkHttpClient.Builder()
          .addInterceptor { chain ->
            val newRequest = chain.request()
                .newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                .build()
            chain.proceed(newRequest)
          }
          .addInterceptor(logger)
          .addInterceptor(crumbInterceptor)
          .cookieJar(cookieJar)
          .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
          .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
          .build()
    return okHttpClient
  }

  @Provides @Singleton internal fun provideGson(): Gson {
    return GsonBuilder().setLenient()
        .create()
  }

  @Provides @Singleton internal fun provideGsonFactory(gson: Gson): GsonConverterFactory {
    return GsonConverterFactory.create(gson)
  }

  @Provides @Singleton internal fun provideSuggestionsApi(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory
  ): SuggestionApi {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.suggestions_endpoint))
        .addConverterFactory(converterFactory)
        .build()
    return retrofit.create(SuggestionApi::class.java)
  }

  @Provides @Singleton internal fun provideYahooFinance(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory
  ): YahooFinance {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.yahoo_endpoint))
        .addConverterFactory(converterFactory)
        .build()
    val yahooFinance = retrofit.create(YahooFinance::class.java)
    return yahooFinance
  }

  @Provides @Singleton internal fun provideYahooFinanceInitialLoad(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient
  ): YahooFinanceInitialLoad {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(context.getString(R.string.yahoo_initial_load_endpoint))
        .build()
    val yahooFinance = retrofit.create(YahooFinanceInitialLoad::class.java)
    return yahooFinance
  }

  @Provides @Singleton internal fun provideYahooFinanceCrumb(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient
  ): YahooFinanceCrumb {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(context.getString(R.string.yahoo_endpoint))
        .build()
    val yahooFinance = retrofit.create(YahooFinanceCrumb::class.java)
    return yahooFinance
  }

  @Provides @Singleton internal fun provideYahooQuoteDetailsApi(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory
  ): YahooQuoteDetails {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.yahoo_endpoint_quote_details))
        .addConverterFactory(converterFactory)
        .build()
    val yahooFinance = retrofit.create(YahooQuoteDetails::class.java)
    return yahooFinance
  }

  @Provides @Singleton internal fun provideApeWisdom(
    @ApplicationContext context: Context,
    okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory
  ): ApeWisdom {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.apewisdom_endpoint))
        .addConverterFactory(converterFactory)
        .build()
    val apewisdom = retrofit.create(ApeWisdom::class.java)
    return apewisdom
  }

  @Provides @Singleton internal fun provideYahooFinanceMostActive(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient,
  ): YahooFinanceMostActive {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.yahoo_finance_endpoint))
        .addConverterFactory(JsoupConverterFactory())
        .build()
    return retrofit.create(YahooFinanceMostActive::class.java)
  }

    @Provides @Singleton internal fun provideYahooFinanceIndianMostActive(
        @ApplicationContext context: Context,
        @Named("yahoo") okHttpClient: OkHttpClient,
    ): YahooIndianStockMostActive {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(context.getString(R.string.yahoo_finance_endpoint))
            .addConverterFactory(JsoupConverterFactory())
            .build()
        return retrofit.create(YahooIndianStockMostActive::class.java)
    }


    @Provides @Singleton internal fun provideYahooFinanceCrypto(
        @ApplicationContext context: Context,
        @Named("yahoo") okHttpClient: OkHttpClient,
    ): YahooFinanceCrypto {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(context.getString(R.string.yahoo_finance_endpoint))
            .addConverterFactory(JsoupConverterFactory())
            .build()
        return retrofit.create(YahooFinanceCrypto::class.java)
    }

    @Provides @Singleton internal fun provideYahooFinanceIndices(
        @ApplicationContext context: Context,
        @Named("yahoo") okHttpClient: OkHttpClient,
    ): YahooFinanceIndices {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(context.getString(R.string.yahoo_finance_endpoint))
            .addConverterFactory(JsoupConverterFactory())
            .build()
        return retrofit.create(YahooFinanceIndices::class.java)
    }



    @Provides @Singleton internal fun provideGoogleNewsApi(
    @ApplicationContext context: Context,
    okHttpClient: OkHttpClient
  ): GoogleNewsApi {
    val retrofit =
      Retrofit.Builder()
          .client(okHttpClient)
          .baseUrl(context.getString(R.string.google_news_endpoint))
          .addConverterFactory(SimpleXmlConverterFactory.create())
          .build()
    return retrofit.create(GoogleNewsApi::class.java)
  }

  @Provides @Singleton internal fun provideYahooFinanceNewsApi(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient
  ): YahooFinanceNewsApi {
    val retrofit =
      Retrofit.Builder()
          .client(okHttpClient)
          .baseUrl(context.getString(R.string.yahoo_news_endpoint))
          .addConverterFactory(SimpleXmlConverterFactory.create())
          .build()
    return retrofit.create(YahooFinanceNewsApi::class.java)
  }

  @Provides @Singleton internal fun provideHistoricalDataApi(
    @ApplicationContext context: Context,
    @Named("yahoo") okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory
  ): ChartApi {
    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.historical_data_endpoint))
        .addConverterFactory(converterFactory)
        .build()
    val api = retrofit.create(ChartApi::class.java)
    return api
  }

}
