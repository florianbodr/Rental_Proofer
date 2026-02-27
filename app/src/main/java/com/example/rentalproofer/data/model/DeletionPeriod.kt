package com.example.rentalproofer.data.model

enum class DeletionPeriod(val days: Int, val label: String) {
    TWO_WEEKS(14, "2 weeks"),
    ONE_MONTH(30, "1 month"),
    THREE_MONTHS(90, "3 months"),
    SIX_MONTHS(180, "6 months"),
    ONE_YEAR(365, "1 year"),
    TWO_YEARS(730, "2 years");

    companion object {
        fun fromDays(days: Int): DeletionPeriod =
            entries.find { it.days == days } ?: SIX_MONTHS
    }
}
