package com.example.codriving.listener

interface IFirebaseCallbackListener<T> {
    fun onSuccess(data: T)
    fun onFailure(exception: Exception)

}