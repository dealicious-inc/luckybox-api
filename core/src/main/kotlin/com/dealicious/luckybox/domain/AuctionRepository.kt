package com.dealicious.luckybox.domain

import com.dealicious.luckybox.domain.Auction
import com.dealicious.luckybox.domain.AuctionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuctionRepository : JpaRepository<Auction, Long> {
    fun findBySellerId(sellerId: Long): List<Auction>
    fun findByStatus(status: AuctionStatus): List<Auction>
}