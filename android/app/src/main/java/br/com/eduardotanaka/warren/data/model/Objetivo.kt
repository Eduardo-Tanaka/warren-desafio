package br.com.eduardotanaka.warren.data.model

import java.time.LocalDate

data class Objetivo(
    val _id: String,
    val name: String,
    val background: Background,
    val totalBalance: Double,
    val goalAmount: Double,
    val goalDate: LocalDate
)