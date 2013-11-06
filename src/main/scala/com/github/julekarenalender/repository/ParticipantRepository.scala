package com.github.julekarenalender.repository

import com.github.julekarenalender.Participant

trait ParticipantRepository extends Repository {
  type T = Participant
}
