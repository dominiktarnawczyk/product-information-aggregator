package com.aggregator.adapters.api

import com.aggregator.ports.InvalidLocaleException
import com.aggregator.ports.locale.LocaleValidator.Companion.validateLocale
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.TimeoutCancellationException
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler(
    private val localizedMessageService: LocalizedMessageService
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
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponse(localizedMessageService.getTimeoutMessage(locale)))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error { "Catalog service failed - critical: ${ex.message}" }
        val locale = getLocaleFromRequest(request)
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponse(localizedMessageService.getCatalogCriticalMessage(locale)))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Aggregation failed: ${ex.message}" }
        val locale = getLocaleFromRequest(request)
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponse(localizedMessageService.getServiceUnavailableMessage(locale, ex.message)))
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

