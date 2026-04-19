package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.data.repository.StudyRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyCard
import javax.inject.Inject

class GetDueCardsUseCase @Inject constructor(
    private val studyRepository: StudyRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        direction: LearningDirection,
        limit: Int = DEFAULT_LIMIT,
        categoryId: Long? = null
    ): List<StudyCard> = studyRepository.getDueCards(
        direction = direction,
        now = clock.nowMillis(),
        limit = limit,
        categoryId = categoryId
    )

    private companion object {
        const val DEFAULT_LIMIT = 50
    }
}
