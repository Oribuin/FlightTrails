package xyz.oribuin.flighttrails.migration

import xyz.oribuin.orilibrary.database.DatabaseConnector
import xyz.oribuin.orilibrary.database.SQLiteConnector
import java.sql.Connection

class ModifyTable : DataMigration(2) {

    override fun migrate(connector: DatabaseConnector?, connection: Connection) {
        if (connection is SQLiteConnector) {
            connection.createStatement().execute("ALTER TABLE flighttrails_data ADD COLUMN transitionColor VARCHAR(7)")
            return
        }

        connection.createStatement().execute("ALTER TABLE flighttrails_data MODIFY transitionColor VARCHAR(7)")
    }

}