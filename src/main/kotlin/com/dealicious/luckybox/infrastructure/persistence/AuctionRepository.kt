package com.dealicious.luckybox.infrastructure.persistence

import com.dealicious.luckybox.domain.model.Auction
import com.dealicious.luckybox.domain.model.AuctionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuctionRepository : JpaRepository<Auction, Long> {
    fun findBySellerId(sellerId: Long): List<Auction>
    fun findByStatus(status: AuctionStatus): List<Auction>
}