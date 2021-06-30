package xyz.oribuin.flighttrails.migration

import xyz.oribuin.orilibrary.database.DatabaseConnector
import java.sql.Connection

class CreateTable(val tablePrefix: String) : DataMigration(1) {

    override fun migrate(connector: DatabaseConnector?, connection: Connection) {
        connection.createStatement().use {
            it.execute(
                "CREATE TABLE IF NOT EXISTS ${tablePrefix}data (" +
                        "player VARCHAR(40)," +
                        "enabled BOOLEAN," +
                        "particle LONGTEXT," +
                        "color VARCHAR(7)," +
                        "blockData VARCHAR(50)," +
                        "itemData VARCHAR (50)," +
                        "note INT," +
                        "PRIMARY KEY(player))"
            )
        }

    }
}