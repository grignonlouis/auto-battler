package com.projet.auto_battler.exception;

import java.util.Map;

public record ApiError(long timestamp, int status, String error, String message, String path, Map<String, String> fieldErrors) { }