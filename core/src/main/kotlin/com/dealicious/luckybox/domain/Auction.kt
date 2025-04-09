package com.dealicious.luckybox.domain

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "auction")
data class Auction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val description: String,

    @Column(nullable = false)
    val startPrice: Long,

    @Column(nullable = false)
    val currentPrice: Long,

    @Column(nullable = false)
    val startTime: LocalDateTime,

    @Column(nullable = false)
    val endTime: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: AuctionStatus,

    // FIXME: sellerId는 User 엔티티와 연관관계로 변경해야 함
    @Column(nullable = false)
    val sellerId: Long
): BaseEntity()

enum class AuctionStatus {
    PENDING,    // 경매 시작 전
    ONGOING,    // 경매 진행 중
    COMPLETED,  // 경매 완료
    CANCELLED   // 경매 취소
}