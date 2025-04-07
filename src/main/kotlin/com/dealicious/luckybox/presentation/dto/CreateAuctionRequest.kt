package com.dealicious.luckybox.presentation.dto

import java.time.LocalDateTime

data class CreateAuctionRequest(
    val title: String,
    val description: String,
    val startPrice: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val sellerId: Long
) 