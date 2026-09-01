package com.memora.core.common.constant

object ConfidenceThresholds {
    const val HIGH = 0.90      // Green — reliable
    const val MEDIUM = 0.70    // Amber — acceptable, no prompt
    const val LOW = 0.70       // Below this → highlight + "Verify this" prompt
}
