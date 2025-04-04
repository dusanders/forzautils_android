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

fun Float.toPrecision(precision: Int): Float {
  if(this.isNaN() || this.isInfinite()) {
    return 0f
  }
  val bigDecimalValue = java.math.BigDecimal(toDouble()).setScale(precision, java.math.RoundingMode.HALF_UP)
  return bigDecimalValue.toFloat()
}