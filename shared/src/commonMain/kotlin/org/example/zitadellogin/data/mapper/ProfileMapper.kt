package org.example.zitadellogin.data.mapper

import org.example.zitadellogin.data.dto.WhoAmiDto
import org.example.zitadellogin.domain.model.UserProfile

fun WhoAmiDto.toDomain(): UserProfile? {
    val id = userIdCamel ?: user_id ?: sub ?: return null
    val userName = username ?: preferred_username
    return UserProfile(
        userId = id,
        username = userName,
        displayName = name,
        email = email,
    )
}
