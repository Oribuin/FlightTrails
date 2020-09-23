package xyz.oribuin.flighttrails.library

abstract class Manager(val plugin: OriPlugin) {
    abstract fun reload()
    abstract fun disable()
}