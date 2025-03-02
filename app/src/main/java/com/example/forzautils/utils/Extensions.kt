package com.example.forzautils.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
  observe(lifecycleOwner, object : Observer<T> {
    override fun onChanged(value: T) {
      observer.onChanged(value);
      removeObserver(this);
    }
  })
}

fun <T> LiveData<T>.observeUntil(
  lifecycleOwner: LifecycleOwner,
  predicate: (T) -> Boolean,
  observer: Observer<T>
) {
  observe(lifecycleOwner, object : Observer<T> {
    override fun onChanged(value: T) {
      observer.onChanged(value)
      if (predicate(value)) {
        removeObserver(this)
      }
    }
  })
}