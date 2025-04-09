package com.dealicious.luckybox.application.service

import com.dealicious.luckybox.domain.model.Auction
import com.dealicious.luckybox.domain.model.AuctionStatus
import com.dealicious.luckybox.infrastructure.persistence.AuctionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class AuctionService(
    private val auctionRepository: AuctionRepository
) {

    @Transactional
    fun create(
        title: String,
        description: String,
        startPrice: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        sellerId: Long
    ): Auction {
        val auction = Auction(
            id = 0L,
            title = title,
            description = description,
            startPrice = startPrice,
            currentPrice = startPrice,
            startTime = startTime,
            endTime = endTime,
            status = AuctionStatus.PENDING,
            sellerId = sellerId
        )
        return auctionRepository.save(auction)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): Optional<Auction> {
        return auctionRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Auction> {
        return auctionRepository.findAll()
    }

    @Transactional
    fun update(id: Long, auction: Auction): Auction {
        val existingAuction = findById(id).orElseThrow { RuntimeException("Auction not found") }
        return auctionRepository.save(auction.copy(id = existingAuction.id))
    }

    @Transactional
    fun delete(id: Long) {
        print("delete auction $id")
        auctionRepository.deleteById(id)
    }
} 