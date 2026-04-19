package com.muhammetkocak.turkcekelimeapp.data.mapper

import com.muhammetkocak.turkcekelimeapp.data.local.dao.DueCardView
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CardStateEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CategoryEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.WordEntity
import com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyCard
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import com.muhammetkocak.turkcekelimeapp.domain.srs.Sm2State

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    nameTr = nameTr,
    nameEn = nameEn,
    emoji = emoji,
    colorHex = colorHex,
    orderIndex = orderIndex
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    nameTr = nameTr,
    nameEn = nameEn,
    emoji = emoji,
    colorHex = colorHex,
    orderIndex = orderIndex
)

fun WordEntity.toDomain(): Word = Word(
    id = id,
    foreignTerm = foreignTerm,
    turkishTerm = turkishTerm,
    partOfSpeech = partOfSpeech,
    exampleForeign = exampleForeign,
    exampleTurkish = exampleTurkish,
    ipa = ipa,
    categoryId = categoryId,
    isUserCreated = isUserCreated,
    isFavorite = isFavorite,
    createdAt = createdAt
)

fun Word.toEntity(): WordEntity = WordEntity(
    id = id,
    foreignTerm = foreignTerm,
    turkishTerm = turkishTerm,
    partOfSpeech = partOfSpeech,
    exampleForeign = exampleForeign,
    exampleTurkish = exampleTurkish,
    ipa = ipa,
    categoryId = categoryId,
    isUserCreated = isUserCreated,
    isFavorite = isFavorite,
    createdAt = createdAt
)

fun CardStateEntity.toDomain(): Sm2State = Sm2State(
    easiness = easiness,
    intervalDays = intervalDays,
    repetition = repetition,
    dueAt = dueAt,
    lastReviewedAt = lastReviewedAt,
    mastery = CardMastery.fromRaw(mastery)
)

fun Sm2State.toEntity(wordId: Long, direction: LearningDirection): CardStateEntity =
    CardStateEntity(
        wordId = wordId,
        direction = direction.raw,
        easiness = easiness,
        intervalDays = intervalDays,
        repetition = repetition,
        dueAt = dueAt,
        lastReviewedAt = lastReviewedAt,
        mastery = mastery.raw
    )

fun DueCardView.toStudyCard(): StudyCard = StudyCard(
    word = Word(
        id = wordId,
        foreignTerm = foreignTerm,
        turkishTerm = turkishTerm,
        partOfSpeech = partOfSpeech,
        exampleForeign = exampleForeign,
        exampleTurkish = exampleTurkish,
        ipa = ipa,
        categoryId = categoryId,
        isFavorite = isFavorite,
        isUserCreated = false,
        createdAt = 0L
    ),
    direction = LearningDirection.fromRaw(direction),
    state = Sm2State(
        easiness = easiness,
        intervalDays = intervalDays,
        repetition = repetition,
        dueAt = dueAt,
        lastReviewedAt = lastReviewedAt,
        mastery = CardMastery.fromRaw(mastery)
    )
)
