package com.zera.android.model.entity.notification

enum class AlertKind {
    STORAGE,
    TIME,
    WARRANTY_EXPIRATION,
    LIFESPAN_EXPIRATION,
    USAGE_INTENSITY_LIMIT,
    STOCK_QUANTITY_LIMIT,
    TIME_IN_STOCK_LIMIT,
    STALE_ITEM,
    RECYCLABLE_TO_LANDFILL,
    PREDICTED_FAILURE,
    ITEM_APPROVED,
    ITEM_REJECTED,
    ;

    companion object {
        fun from(raw: String): AlertKind? =
            entries.firstOrNull { it.name == raw }
    }
}
