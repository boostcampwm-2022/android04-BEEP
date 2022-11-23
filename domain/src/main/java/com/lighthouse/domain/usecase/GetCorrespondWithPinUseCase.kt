package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCorrespondWithPinUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend operator fun invoke(pinString: String): Boolean {
        val correctPinString = userPreferencesRepository.getPin()
        val result = correctPinString.first()

        println("DATASTORE 저장된 pinString: $result")
        return pinString == result
    }
}
