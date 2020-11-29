package com.github.julekarenalender.domain

data class GameParameters(
        val days: List<Int>,
        val bonus: Boolean,
        val dryRun: Boolean = false
)

data class Game(
        val participants: List<ParticipantData> = emptyList(),
        val gameParameters: GameParameters
)
