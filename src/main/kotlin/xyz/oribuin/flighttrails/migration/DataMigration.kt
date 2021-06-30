package xyz.oribuin.flighttrails.migration

import xyz.oribuin.orilibrary.database.DatabaseConnector
import java.sql.Connection
import java.sql.SQLException

abstract class DataMigration {

    /**
     * Migrates the database to this migration stage
     *
     * @param connector The connector for the database
     * @param connection The connection to the database
     * @throws SQLException Any error that occurs during the SQL execution
     */
    @Throws(SQLException::class)
    abstract fun migrate(connector: DatabaseConnector?, connection: Connection)

}