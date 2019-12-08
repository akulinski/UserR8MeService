package com.akulinski.pushservice.core.domain

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "follower")
class FollowerEntity(@Column var username: String? = null,
                     @ManyToOne(fetch = FetchType.LAZY, optional = false)
                     @JoinColumn(name = "status_change_id", nullable = false)
                     @OnDelete(action = OnDeleteAction.CASCADE)
                     var statusChangeEntity: StatusChangeEntity? = null) {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long? = null

}
