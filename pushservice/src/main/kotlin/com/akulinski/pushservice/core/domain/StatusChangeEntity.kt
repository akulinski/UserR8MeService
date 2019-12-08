package com.akulinski.pushservice.core.domain

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*


@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "status_change")
class StatusChangeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "status_change_id")
  var id: Long? = null

  @Column
  @Enumerated(EnumType.STRING)
  var changeDirection: ChangeDirection? = ChangeDirection.NONE

  @Column
  var oldValue: Double? = null

  @Column
  var newValue: Double? = null

  @Column
  var username: String? = null

  @OneToMany(mappedBy = "statusChangeEntity")
  var followers: Set<FollowerEntity>? = null

}
