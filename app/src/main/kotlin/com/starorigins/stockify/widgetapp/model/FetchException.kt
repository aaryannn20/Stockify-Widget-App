package com.starorigins.stockify.widgetapp.model

import java.io.IOException

class FetchException(message: String, ex: Throwable? = null): IOException(message, ex)