package at.donrobo.model

import java.io.Serializable
import java.util.*

abstract class DeckbuilderObject : Serializable {
    val uuid: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeckbuilderObject) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    abstract val defaultWidth: Double
    abstract val defaultHeight: Double
}