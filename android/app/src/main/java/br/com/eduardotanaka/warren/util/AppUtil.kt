package br.com.eduardotanaka.warren.util

import android.os.Looper

fun onMainThread() =  Looper.myLooper() == Looper.getMainLooper()
