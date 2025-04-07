package com.dealicious.luckybox.presentation.dto

import com.dealicious.luckybox.domain.model.Auction
import java.time.LocalDateTime

data class AuctionResponse(
    val id: Long,
    val title: String,
    val description: String,
    val startPrice: Long,
    val currentPrice: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: String,
    val sellerId: Long
) {
    companion object {
        fun from(auction: Auction): AuctionResponse {
            return AuctionResponse(
                id = auction.id,
                title = auction.title,
                description = auction.description,
                startPrice = auction.startPrice,
                currentPrice = auction.currentPrice,
                startTime = auction.startTime,
                endTime = auction.endTime,
                status = auction.status.name,
                sellerId = auction.sellerId
            )
        }
    }
} 