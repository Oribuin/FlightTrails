package xyz.oribuin.flighttrails.library

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.library.NMSUtil.versionNumber
import java.awt.Color
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.math.roundToInt

object HexUtils {
    private const val CHARS_UNTIL_LOOP = 30
    private val LOOP_VALUES = listOf("l", "L", "loop")
    private val RAINBOW_PATTERN = Pattern.compile("<(rainbow|r)(:\\d*\\.?\\d+){0,2}(:(l|L|loop))?>")
    private val GRADIENT_PATTERN = Pattern.compile("<(gradient|g)(:#([A-Fa-f0-9]){6}){2,}(:(l|L|loop))?>")
    private val HEX_PATTERNS = listOf(
        Pattern.compile("<#([A-Fa-f0-9]){6}>"),  // <#FFFFFF>
        Pattern.compile("\\{#([A-Fa-f0-9]){6}}"),  // {#FFFFFF}
        Pattern.compile("&#([A-Fa-f0-9]){6}"),  // &#FFFFFF
        Pattern.compile("#([A-Fa-f0-9]){6}") // #FFFFFF
    )
    private val STOP = Pattern.compile(
        "<(gradient|g)(:#([A-Fa-f0-9]){6}){2,}(:(l|L|loop))?>|" +
                "<(rainbow|r)(:\\d*\\.?\\d+){0,2}(:(l|L|loop))?>|" +
                "(&[a-f0-9r])|" +
                "<#([A-Fa-f0-9]){6}>|" +
                "\\{#([A-Fa-f0-9]){6}}|" +
                "&#([A-Fa-f0-9]){6}|" +
                "#([A-Fa-f0-9]){6}|" +
                org.bukkit.ChatColor.COLOR_CHAR
    )

    /**
     * Sends a CommandSender a colored message
     *
     * @param sender  The CommandSender to send to
     * @param message The message to send
     */
    fun sendMessage(sender: CommandSender, message: String) {
        sender.sendMessage(colorify(message))
    }

    /**
     * Parses gradients, hex colors, and legacy color codes
     *
     * @param message The message
     * @return A color-replaced message
     */
    fun colorify(message: String): String {
        var parsed = message
        parsed = parseRainbow(parsed)
        parsed = parseGradients(parsed)
        parsed = parseHex(parsed)
        parsed = parseLegacy(parsed)
        return parsed
    }

    private fun parseRainbow(message: String): String {
        var parsed = message
        var matcher = RAINBOW_PATTERN.matcher(parsed)
        while (matcher.find()) {
            val parsedRainbow = StringBuilder()
            val match = matcher.group()
            val tagLength = if (match.startsWith("<ra")) 8 else 2
            val indexOfClose = match.indexOf(">")
            var extraDataContent = match.substring(tagLength, indexOfClose)
            var looping = false
            var extraData: DoubleArray
            if (extraDataContent.isNotEmpty()) {
                extraDataContent = extraDataContent.substring(1)
                if (LOOP_VALUES.stream().anyMatch { suffix: String -> extraDataContent.endsWith((suffix)) }) {
                    looping = true
                    extraDataContent = if (extraDataContent.contains(":")) {
                        extraDataContent.substring(0, extraDataContent.lastIndexOf(":"))
                    } else {
                        ""
                    }
                }
                extraData = Arrays.stream(extraDataContent.split(":".toRegex()).toTypedArray()).filter { x -> x.isNotEmpty() }.mapToDouble { s -> s.toDouble() }.toArray()
            } else {
                extraData = DoubleArray(0)
            }
            val saturation = if (extraData.size > 0) extraData[0].toFloat() else 1.0f
            val brightness = if (extraData.size > 1) extraData[1].toFloat() else 1.0f
            val stop = findStop(parsed, matcher.end())
            val content = parsed.substring(matcher.end(), stop)
            var contentLength = content.length
            val chars = content.toCharArray()
            for (i in 0 until chars.size - 1) if (chars[i] == '&' && "KkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) contentLength -= 2
            val length = if (looping) Math.min(contentLength, CHARS_UNTIL_LOOP) else contentLength
            val rainbow = Rainbow(length, saturation, brightness)
            var compoundedFormat = "" // Carry the format codes through the rainbow gradient
            var i = 0
            while (i < chars.size) {
                val c = chars[i]
                if (c == '&' && i + 1 < chars.size) {
                    val next = chars[i + 1]
                    val color = org.bukkit.ChatColor.getByChar(next)
                    if (color != null && color.isFormat) {
                        compoundedFormat += ChatColor.COLOR_CHAR.toString() + next
                        i++ // Skip next character
                        i++
                        continue
                    }
                }
                parsedRainbow.append(translateHex(rainbow.next())).append(compoundedFormat).append(c)
                i++
            }
            val before = parsed.substring(0, matcher.start())
            val after = parsed.substring(stop)
            parsed = before + parsedRainbow + after
            matcher = RAINBOW_PATTERN.matcher(parsed)
        }
        return parsed
    }

    private fun parseGradients(message: String): String {
        var parsed = message
        var matcher = GRADIENT_PATTERN.matcher(parsed)
        while (matcher.find()) {
            val parsedGradient = StringBuilder()
            val match = matcher.group()
            val tagLength = if (match.startsWith("<gr")) 10 else 3
            val indexOfClose = match.indexOf(">")
            var hexContent = match.substring(tagLength, indexOfClose)
            var looping = false
            if (LOOP_VALUES.stream().anyMatch { suffix -> hexContent.endsWith((suffix)) }) {
                looping = true
                hexContent = hexContent.substring(0, hexContent.lastIndexOf(":"))
            }
            val hexSteps = Arrays.stream(hexContent.split(":".toRegex()).toTypedArray()).map { nm -> Color.decode(nm) }.collect(Collectors.toList())
            val stop = findStop(parsed, matcher.end())
            val content = parsed.substring(matcher.end(), stop)
            var contentLength = content.length
            val chars = content.toCharArray()
            for (i in 0 until chars.size - 1) if (chars[i] == '&' && "KkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) contentLength -= 2
            val length = if (looping) Math.min(contentLength, CHARS_UNTIL_LOOP) else contentLength
            val gradient = Gradient(hexSteps, length)
            var compoundedFormat: String = "" // Carry the format codes through the gradient
            var i = 0
            while (i < chars.size) {
                val c = chars[i]
                if (c == '&' && i + 1 < chars.size) {
                    val next = chars[i + 1]
                    val color = org.bukkit.ChatColor.getByChar(next)
                    if (color != null && color.isFormat) {
                        compoundedFormat += ChatColor.COLOR_CHAR.toString() + next
                        i++ // Skip next character
                        i++
                        continue
                    }
                }
                parsedGradient.append(translateHex(gradient.next())).append(compoundedFormat).append(c)
                i++
            }
            val before = parsed.substring(0, matcher.start())
            val after = parsed.substring(stop)
            parsed = before + parsedGradient + after
            matcher = GRADIENT_PATTERN.matcher(parsed)
        }
        return parsed
    }

    private fun parseHex(message: String): String {
        var parsed = message
        for (pattern: Pattern in HEX_PATTERNS) {
            var matcher = pattern.matcher(parsed)
            while (matcher.find()) {
                val color = translateHex(cleanHex(matcher.group()))
                val before = parsed.substring(0, matcher.start())
                val after = parsed.substring(matcher.end())
                parsed = before + color + after
                matcher = pattern.matcher(parsed)
            }
        }
        return parsed
    }

    private fun parseLegacy(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    /**
     * Returns the index before the color changes
     *
     * @param content     The content to search through
     * @param searchAfter The index at which to search after
     * @return the index of the color stop, or the end of the string index if none is found
     */
    private fun findStop(content: String, searchAfter: Int): Int {
        val matcher = STOP.matcher(content)
        while (matcher.find()) {
            if (matcher.start() > searchAfter) return matcher.start()
        }
        return content.length
    }

    private fun cleanHex(hex: String): String {
        if (hex.startsWith("<") || hex.startsWith("{")) {
            return hex.substring(1, hex.length - 1)
        } else return if (hex.startsWith("&")) {
            hex.substring(1)
        } else {
            hex
        }
    }

    /**
     * Finds the closest hex or ChatColor value as the hex string
     *
     * @param hex The hex color
     * @return The closest ChatColor value
     */
    private fun translateHex(hex: String): String {
        return if (versionNumber >= 16) ChatColor.of(hex).toString() else translateHex(Color.decode(hex))
    }

    private fun translateHex(color: Color): String {
        if (versionNumber >= 16) return ChatColor.of(color).toString()
        var minDist = Int.MAX_VALUE
        var legacy = ChatColor.WHITE
        for (mapping: ChatColorHexMapping in ChatColorHexMapping.values()) {
            val r = mapping.red - color.red
            val g = mapping.green - color.green
            val b = mapping.blue - color.blue
            val dist = (r * r) + (g * g) + (b * b)
            if (dist < minDist) {
                minDist = dist
                legacy = mapping.chatColor
            }
        }
        return legacy.toString()
    }

    /**
     * Maps hex codes to ChatColors
     */
    private enum class ChatColorHexMapping(hex: Int, val chatColor: ChatColor) {
        BLACK(0x000000, ChatColor.BLACK), DARK_BLUE(0x0000AA, ChatColor.DARK_BLUE), DARK_GREEN(0x00AA00, ChatColor.DARK_GREEN), DARK_AQUA(0x00AAAA, ChatColor.DARK_AQUA), DARK_RED(0xAA0000, ChatColor.DARK_RED), DARK_PURPLE(0xAA00AA, ChatColor.DARK_PURPLE), GOLD(0xFFAA00, ChatColor.GOLD), GRAY(0xAAAAAA, ChatColor.GRAY), DARK_GRAY(0x555555, ChatColor.DARK_GRAY), BLUE(0x5555FF, ChatColor.BLUE), GREEN(0x55FF55, ChatColor.GREEN), AQUA(0x55FFFF, ChatColor.AQUA), RED(0xFF5555, ChatColor.RED), LIGHT_PURPLE(0xFF55FF, ChatColor.LIGHT_PURPLE), YELLOW(0xFFFF55, ChatColor.YELLOW), WHITE(0xFFFFFF, ChatColor.WHITE);

        val red = (hex shr 16) and 0xFF
        val green = (hex shr 8) and 0xFF
        val blue = hex and 0xFF

    }

    /**
     * Allows generation of a multi-part gradient with a defined number of steps
     */
    class Gradient(colors: List<Color>, steps: Int) {
        private val gradients: MutableList<TwoStopGradient>
        private val steps: Int
        private var step: Int
        private var reversed: Boolean

        /**
         * @return the next color in the gradient
         */
        operator fun next(): Color {
            // Gradients will use the first color if the entire spectrum won't be available to preserve prettiness
            if (versionNumber < 16 || steps <= 1) return gradients[0].colorAt(0)
            val color: Color
            if (gradients.size < 2) {
                color = gradients[0].colorAt(step)
            } else {
                val segment = steps.toFloat() / gradients.size
                val index = Math.min(Math.floor((step / segment).toDouble()), (gradients.size - 1).toDouble()).toInt()
                color = gradients[index].colorAt(step)
            }

            // Increment/Loop the step to keep it rotating through the gradients
            if (!reversed) {
                step++
                if (step >= steps) reversed = true
            } else {
                step--
                if (step <= 0) reversed = false
            }
            return color
        }

        private class TwoStopGradient(private val startColor: Color, private val endColor: Color, private val lowerRange: Float, private val upperRange: Float) {
            /**
             * Gets the color of this gradient at the given step
             *
             * @param step The step
             * @return The color of this gradient at the given step
             */
            fun colorAt(step: Int): Color {
                return Color(
                    calculateHexPiece(step, startColor.red, endColor.red),
                    calculateHexPiece(step, startColor.green, endColor.green),
                    calculateHexPiece(step, startColor.blue, endColor.blue)
                )
            }

            private fun calculateHexPiece(step: Int, channelStart: Int, channelEnd: Int): Int {
                val range = upperRange - lowerRange
                val interval = (channelEnd - channelStart) / range
                return (interval * (step - lowerRange) + channelStart).roundToInt()
            }
        }

        init {
            if (colors.size < 2) throw IllegalArgumentException("Must provide at least 2 colors")
            gradients = ArrayList()
            this.steps = steps - 1
            step = 0
            reversed = false
            val increment = this.steps.toFloat() / (colors.size - 1)
            for (i in 0 until colors.size - 1) gradients.add(TwoStopGradient(colors[i], colors[i + 1], increment * i, increment * (i + 1)))
        }
    }

    /**
     * Allows generation of a rainbow gradient with a fixed numbef of steps
     */
    class Rainbow @JvmOverloads constructor(totalColors: Int, saturation: Float = 1.0f, brightness: Float = 1.0f) {
        private val hueStep: Float
        private val saturation: Float
        private val brightness: Float
        private var hue: Float

        /**
         * @return the next color in the gradient
         */
        operator fun next(): Color {
            val color = Color.getHSBColor(hue, saturation, brightness)
            hue += hueStep
            return color
        }

        init {
            if (totalColors < 1) throw IllegalArgumentException("Must have at least 1 total color")
            if (0.0f > saturation || saturation > 1.0f) throw IllegalArgumentException("Saturation must be between 0.0 and 1.0")
            if (0.0f > brightness || brightness > 1.0f) throw IllegalArgumentException("Brightness must be between 0.0 and 1.0")
            hueStep = 1.0f / totalColors
            this.saturation = saturation
            this.brightness = brightness
            hue = 0f
        }
    }
}

internal object NMSUtil {
    private var cachedVersion: String? = null
    private var cachedVersionNumber = -1

    /**
     * @return The server version
     */
    private val version: String
        get() {
            if (cachedVersion == null) {
                val name = Bukkit.getServer().javaClass.getPackage().name
                cachedVersion = name.substring(name.lastIndexOf('.') + 1)
            }
            return cachedVersion ?: ""
        }

    /**
     * @return the server version major release number
     */
    val versionNumber: Int
        get() {
            if (cachedVersionNumber == -1) {
                val name = version.substring(3)
                cachedVersionNumber = name.substring(0, name.length - 3).toInt()
            }
            return cachedVersionNumber
        }
}
