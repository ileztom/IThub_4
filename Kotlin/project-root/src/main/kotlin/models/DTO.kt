package com.example.models

import java.util.UUID

data class UserDTO(val id: UUID, val username: String, val role: Role)
enum class Role { ADMIN, USER }
