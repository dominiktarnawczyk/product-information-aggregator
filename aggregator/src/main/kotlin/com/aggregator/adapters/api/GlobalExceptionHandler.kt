package com.aggregator.adapters.api

import com.aggregator.ports.locale.MessageLocalizer
import com.aggregator.ports.providers.CatalogServiceUnrespondingException
import com.aggregator.ports.locale.InvalidLocaleException
import com.aggregator.ports.locale.LocaleValidator.Companion.validateLocale
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.TimeoutCancellationException
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageLocalizer: MessageLocalizer
) {

    @ExceptionHandler(InvalidLocaleException::class)
    fun handleInvalidLocaleException(ex: InvalidLocaleException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error { "Invalid locale: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Invalid locale"))
    }

    @ExceptionHandler(TimeoutCancellationException::class)
    fun handleTimeoutException(ex: TimeoutCancellationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error { "Request timeout: ${ex.message}" }
        val locale = getLocaleFromRequest(request)
        return ResponseEntity
            .status(SERVICE_UNAVAILABLE)
            .body(ErrorResponse(messageLocalizer.getTimeoutMessage(locale)))
    }

    @ExceptionHandler(CatalogServiceUnrespondingException::class)
    fun handleCatalogServiceUnrespondingException(ex: CatalogServiceUnrespondingException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error { "Catalog service failed - critical: ${ex.message}" }
        val locale = getLocaleFromRequest(request)
        return ResponseEntity
            .status(SERVICE_UNAVAILABLE)
            .body(ErrorResponse(messageLocalizer.getCatalogCriticalMessage(locale)))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Aggregation failed: ${ex.message}" }
        val locale = getLocaleFromRequest(request)
        return ResponseEntity
            .status(SERVICE_UNAVAILABLE)
            .body(ErrorResponse(messageLocalizer.getServiceUnavailableMessage(locale, ex.message)))
    }

    private fun getLocaleFromRequest(request: HttpServletRequest): Locale {
        val marketLanguage = request.getParameter("market-language")
        return if (marketLanguage != null) {
            validateLocale(marketLanguage)
            Locale.forLanguageTag(marketLanguage)
        } else {
            request.locale ?: Locale.ENGLISH
        }
    }

    companion object : KLogging()
}

data class ErrorResponse(
    val message: String
)

