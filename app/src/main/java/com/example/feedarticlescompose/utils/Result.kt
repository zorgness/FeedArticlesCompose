package com.example.feedarticlescompose.utils

sealed class Result<out T> {
    class Success<T>(val data: T, val httpStatus: Int) :Result<T>()
    class HttpStatus(val httpStatus: Int ):Result<Nothing>()
    class Failure<T>(val state: T) :Result<Nothing>()
    data class ExeptionError<T>(val state: T) : Result<Nothing>()

}