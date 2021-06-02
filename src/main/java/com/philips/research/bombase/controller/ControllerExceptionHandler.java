/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombase.controller;

import com.philips.research.bombase.core.UnknownPackageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts exceptions on REST requests into status responses.
 */
@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handles requested but unknown packages.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnknownPackageException.class)
    public Map<String, String> handleUnknownPackageException(UnknownPackageException e) {
        return Map.of("reason", e.getMessage());
    }

    /**
     * Handles request parameter validation failures.
     *
     * @return BAD_REQUEST with a list of the detected validation failures.
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentTypeMismatchException exception) {
        return Map.of("reason", String.format("Wrong type for parameter '%s'", exception.getName()));
    }

    /**
     * Handles request parameter validation failures.
     *
     * @return BAD_REQUEST with a list of the detected validation failures.
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        //noinspection ConstantConditions
        return exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        (error) -> ((FieldError) error).getField(),
                        DefaultMessageSourceResolvable::getDefaultMessage));
    }
}
