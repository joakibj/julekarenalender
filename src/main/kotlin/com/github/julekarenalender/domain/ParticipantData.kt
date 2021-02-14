package com.github.julekarenalender.domain

import com.github.julekarenalender.db
import com.github.julekarenalender.gameParameters
import com.github.julekarenalender.logger
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.dizitart.no2.objects.ObjectRepository
import java.util.*

@Indices(Index(value = "uuid", type = IndexType.Unique))
data class ParticipantData(
    @Id val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val image: String?,
    var dateWon: Int = 0
) {
    fun save() {
        logger.debug("Updating db with :$this")
        if (gameParameters.dryRun) {
            logger.debug("*** DryRun: No database update")
            return
        }

        val participantStore: ObjectRepository<ParticipantData> = db.getRepository(ParticipantData::class.java)
        val all = participantStore.find().toList()
        val exists = all.stream().anyMatch { it.uuid == this.uuid }

        if (exists) {
            participantStore.update(this)
        } else {
            participantStore.insert(this)
        }
    }

    fun remove() {
        if (gameParameters.dryRun) {
            logger.debug("*** DryRun: No database update")
            return
        }

        val participantStore: ObjectRepository<ParticipantData> = db.getRepository(ParticipantData::class.java)
        if (participantStore.find().contains(this)) {
            participantStore.remove(this)
        }
    }

    fun insertNonexistentFirstTime() {
        if (gameParameters.dryRun) {
            logger.debug("*** DryRun: No database update")
            return
        }

        val participantStore: ObjectRepository<ParticipantData> = db.getRepository(ParticipantData::class.java)
        val all = participantStore.find().toList()

        val exists = all.stream().anyMatch { it.name == this.name }
        if (exists) {
            participantStore.insert(this)
        }

    }

}
