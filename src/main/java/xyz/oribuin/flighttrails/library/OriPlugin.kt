package xyz.oribuin.flighttrails.library

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author Oribuin
 *
 * Reminder: Java Dumb
 */
abstract class OriPlugin : JavaPlugin() {
    private var managers = mutableMapOf<KClass<out Manager>, Manager>()

    abstract fun enablePlugin()

    abstract fun disablePlugin()

    override fun onEnable() {
        saveDefaultConfig()
        enablePlugin()
        reload()
    }

    override fun onDisable() {
        disablePlugin()
    }

    /**
     * Reload the plugin.
     */
    fun reload() {
        disableManagers()
        server.scheduler.cancelTasks(this)
        managers.values.forEach(Consumer { obj: Manager -> obj.reload() })
    }

    /**
     * Register all the plugin listeners.
     *
     * @param listeners The listeners being registered.
     */
    fun registerListeners(vararg listeners: Listener) {
        Arrays.stream(listeners).forEach { listener -> Bukkit.getPluginManager().registerEvents(listener, this) }
    }

    /**
     * Register all the commands listed.
     *
     * @param commands The commands being registered.
     */
    fun registerCommands(vararg commands: OriCommand) {
        Arrays.stream(commands).forEach { cmd -> cmd.register() }
    }

    /**
     * Disable all the managers.
     */
    private fun disableManagers() {
        managers.values.forEach { manager -> manager.disable() }
    }

    fun <M : Manager> getManager(managerClass: KClass<M>): M {
        synchronized(managers) {
            @Suppress("UNCHECKED_CAST")
            if (managers.containsKey(managerClass))
                return managers[managerClass] as M

            return try {
                val manager = managerClass.constructors.first().call(this)
                manager.reload()
                managers[managerClass] = manager
                manager
            } catch (ex: ReflectiveOperationException) {
                error("Failed to load manager for ${managerClass.simpleName}")
            }
        }
    }
}