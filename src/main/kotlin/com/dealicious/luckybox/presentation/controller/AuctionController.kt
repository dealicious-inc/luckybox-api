package com.dealicious.luckybox.presentation.controller

import com.dealicious.luckybox.application.service.AuctionService
import com.dealicious.luckybox.domain.model.Auction
import com.dealicious.luckybox.domain.model.AuctionStatus
import com.dealicious.luckybox.presentation.dto.CreateAuctionRequest
import com.dealicious.luckybox.presentation.dto.AuctionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auctions")
class AuctionController(
    private val auctionService: AuctionService
) {

    @PostMapping
    fun createAuction(@RequestBody request: CreateAuctionRequest): ResponseEntity<AuctionResponse> {
        val createdAuction = auctionService.create(
            title = request.title,
            description = request.description,
            startPrice = request.startPrice,
            startTime = request.startTime,
            endTime = request.endTime,
            sellerId = request.sellerId
        )
        return ResponseEntity.ok(AuctionResponse.from(createdAuction))
    }

    @GetMapping("/{id}")
    fun getAuction(@PathVariable id: Long): ResponseEntity<AuctionResponse> {
        return auctionService.findById(id)
            .orElse(null)
            ?.let { ResponseEntity.ok(AuctionResponse.from(it)) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllAuctions(): ResponseEntity<List<AuctionResponse>> {
        val auctions = auctionService.findAll()
        val responses = auctions.map { AuctionResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @PutMapping("/{id}")
    fun updateAuction(
        @PathVariable id: Long,
        @RequestBody request: CreateAuctionRequest
    ): ResponseEntity<AuctionResponse> {
        val existingAuction = auctionService.findById(id) ?: throw RuntimeException("Auction not found")
        val updatedAuction = auctionService.create(
            title = request.title,
            description = request.description,
            startPrice = request.startPrice,
            startTime = request.startTime,
            endTime = request.endTime,
            sellerId = request.sellerId
        )
        return ResponseEntity.ok(AuctionResponse.from(updatedAuction))
    }

    @DeleteMapping("/{id}")
    fun deleteAuction(@PathVariable id: Long): ResponseEntity<Void> {
        auctionService.delete(id)
        return ResponseEntity.noContent().build()
    }
} 