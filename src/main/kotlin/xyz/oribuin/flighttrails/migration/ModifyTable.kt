package xyz.oribuin.flighttrails.migration

import xyz.oribuin.orilibrary.database.DatabaseConnector
import java.sql.Connection

class ModifyTable : DataMigration(2) {

    override fun migrate(connector: DatabaseConnector?, connection: Connection) {

        connection.prepareStatement("ALTER TABLE flighttrails_data ADD transitionColor VARCHAR(7)").executeUpdate()
    }

}