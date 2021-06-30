package xyz.oribuin.flighttrails.migration

import xyz.oribuin.orilibrary.database.DatabaseConnector
import java.lang.Exception
import java.sql.Connection
import java.sql.SQLException

class ModifyTable : DataMigration(2) {

    override fun migrate(connector: DatabaseConnector?, connection: Connection) {
        try {
            connection.prepareStatement("ALTER TABLE flighttrails_data ADD COLUMN transitionColor VARCHAR(7)").use { it.executeUpdate() }
        } catch (ex: Exception) {
            return
        }

    }

}