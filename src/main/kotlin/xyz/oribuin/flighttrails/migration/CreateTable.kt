package xyz.oribuin.flighttrails.migration

import xyz.oribuin.orilibrary.database.DatabaseConnector
import java.sql.Connection

class CreateTable : DataMigration(1) {

    override fun migrate(connector: DatabaseConnector?, connection: Connection) {
        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS flighttrails_data (" +
                    "player VARCHAR(40)," +
                    "enabled BOOLEAN," +
                    "particle LONGTEXT," +
                    "color VARCHAR(7)," +
                    "blockData VARCHAR(50)," +
                    "itemData VARCHAR (50)," +
                    "note INT," +
                    "PRIMARY KEY(player))"
        ).executeUpdate()

    }
}