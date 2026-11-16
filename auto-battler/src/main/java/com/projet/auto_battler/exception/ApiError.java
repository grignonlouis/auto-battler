package com.projet.auto_battler.exception;

import java.util.Map;

/**
 * Error body returned by the REST API.
 *
 * @param timestamp   when the error happened, in milliseconds
 * @param status      the HTTP status code
 * @param error       the short HTTP status text
 * @param message     the explanation of the error
 * @param path        the request path
 * @param fieldErrors validation messages by field name, empty otherwise
 */
public record ApiError(long timestamp, int status, String error, String message, String path, Map<String, String> fieldErrors) { }