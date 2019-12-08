package com.akulinski.pushservice.core.repositories

import com.akulinski.pushservice.core.domain.StatusChangeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StatusChangeRepository: CrudRepository<StatusChangeEntity, Long>{

}
