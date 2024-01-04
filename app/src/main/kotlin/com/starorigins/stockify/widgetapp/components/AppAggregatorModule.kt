package com.starorigins.stockify.widgetapp.components

import com.starorigins.stockify.widgetapp.network.NetworkModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(
    includes = [
      AppModule::class,
      NetworkModule::class
    ]
)
interface AppAggregatorModule