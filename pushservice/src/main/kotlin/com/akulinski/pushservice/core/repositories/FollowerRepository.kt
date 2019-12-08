package com.akulinski.pushservice.core.repositories

import com.akulinski.pushservice.core.domain.FollowerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowerRepository: CrudRepository<FollowerEntity, Long> {
}
