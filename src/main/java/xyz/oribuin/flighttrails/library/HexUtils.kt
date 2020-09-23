package xyz.oribuin.flighttrails.library

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import java.awt.Color
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.math.floor
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
     * Parses gradients, hex colors, and legacy color codes
     *
     * @param message The message
     * @return A color-replaced message
     */
    @JvmStatic
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
                if (LOOP_VALUES.stream().anyMatch { suffix: String? -> extraDataContent.endsWith(suffix!!) }) {
                    looping = true
                    extraDataContent = if (extraDataContent.contains(":")) {
                        extraDataContent.substring(0, extraDataContent.lastIndexOf(":"))
                    } else {
                        ""
                    }
                }
                extraData = Arrays.stream(extraDataContent.split(":").toTypedArray()).filter { x: String -> x.isNotEmpty() }.mapToDouble { s: String -> s.toDouble() }.toArray()
            } else {
                extraData = DoubleArray(0)
            }
            val saturation = if (extraData.isNotEmpty()) extraData[0].toFloat() else 1.0f
            val brightness = if (extraData.size > 1) extraData[1].toFloat() else 1.0f
            val stop = findStop(parsed, matcher.end())
            val content = parsed.substring(matcher.end(), stop)
            val length = if (looping) content.length.coerceAtMost(CHARS_UNTIL_LOOP) else content.length
            val rainbow = Rainbow(length, saturation, brightness)
            for (c in content.toCharArray()) parsedRainbow.append(translateHex(rainbow.next())).append(c)
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
            if (LOOP_VALUES.stream().anyMatch { suffix: String? -> hexContent.endsWith(suffix!!) }) {
                looping = true
                hexContent = hexContent.substring(0, hexContent.lastIndexOf(":"))
            }
            val hexSteps = Arrays.stream(hexContent.split(":").toTypedArray()).map { nm: String? -> Color.decode(nm) }.collect(Collectors.toList())
            val stop = findStop(parsed, matcher.end())
            val content = parsed.substring(matcher.end(), stop)
            val length = if (looping) content.length.coerceAtMost(CHARS_UNTIL_LOOP) else content.length
            val gradient = Gradient(hexSteps, length)
            for (c in content.toCharArray()) parsedGradient.append(translateHex(gradient.next())).append(c)
            val before = parsed.substring(0, matcher.start())
            val after = parsed.substring(stop)
            parsed = before + parsedGradient + after
            matcher = GRADIENT_PATTERN.matcher(parsed)
        }
        return parsed
    }

    private fun parseHex(message: String): String {
        var parsed = message
        for (pattern in HEX_PATTERNS) {
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
        return if (hex.startsWith("<") || hex.startsWith("{")) {
            hex.substring(1, hex.length - 1)
        } else if (hex.startsWith("&")) {
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
        return if (NMSUtil.versionNumber >= 16) ChatColor.of(hex).toString() else translateHex(Color.decode(hex))
    }

    private fun translateHex(color: Color): String {
        if (NMSUtil.versionNumber >= 16) return ChatColor.of(color).toString()
        var minDist = Int.MAX_VALUE
        var legacy = ChatColor.WHITE
        for (mapping in ChatColorHexMapping.values()) {
            val r = mapping.red - color.red
            val g = mapping.green - color.green
            val b = mapping.blue - color.blue
            val dist = r * r + g * g + b * b
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
    private enum class ChatColorHexMapping(hex: Int, chatColor: ChatColor) {
        BLACK(0x000000, ChatColor.BLACK),
        DARK_BLUE(0x0000AA, ChatColor.DARK_BLUE),
        DARK_GREEN(0x00AA00, ChatColor.DARK_GREEN),
        DARK_AQUA(0x00AAAA, ChatColor.DARK_AQUA),
        DARK_RED(0xAA0000, ChatColor.DARK_RED),
        DARK_PURPLE(0xAA00AA, ChatColor.DARK_PURPLE), GOLD(0xFFAA00, ChatColor.GOLD),
        GRAY(0xAAAAAA, ChatColor.GRAY),
        DARK_GRAY(0x555555, ChatColor.DARK_GRAY),
        BLUE(0x5555FF, ChatColor.BLUE),
        GREEN(0x55FF55, ChatColor.GREEN),
        AQUA(0x55FFFF, ChatColor.AQUA),
        RED(0xFF5555, ChatColor.RED),
        LIGHT_PURPLE(0xFF55FF, ChatColor.LIGHT_PURPLE), YELLOW(0xFFFF55, ChatColor.YELLOW),
        WHITE(0xFFFFFF, ChatColor.WHITE);

        val red: Int = hex shr 16 and 0xFF
        val green: Int = hex shr 8 and 0xFF
        val blue: Int = hex and 0xFF
        val chatColor: ChatColor = chatColor

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
            if (NMSUtil.versionNumber < 16 || steps <= 1) return gradients[0].colorAt(0)
            val color: Color
            color = if (gradients.size < 2) {
                gradients[0].colorAt(step)
            } else {
                val segment = steps.toFloat() / gradients.size
                val index = floor(step / segment.toDouble()).coerceAtMost(gradients.size - 1.toDouble()).toInt()
                gradients[index].colorAt(step)
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
            require(colors.size >= 2) { "Must provide at least 2 colors" }
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
            require(totalColors >= 1) { "Must have at least 1 total color" }
            require(!(0.0f > saturation || saturation > 1.0f)) { "Saturation must be between 0.0 and 1.0" }
            require(!(0.0f > brightness || brightness > 1.0f)) { "Brightness must be between 0.0 and 1.0" }
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
    val version: String?
        get() {
            if (cachedVersion == null) {
                val name = Bukkit.getServer().javaClass.getPackage().name
                cachedVersion = name.substring(name.lastIndexOf('.') + 1)
            }
            return cachedVersion
        }

    /**
     * @return the server version major release number
     */
    val versionNumber: Int
        get() {
            if (cachedVersionNumber == -1) {
                val name = version?.substring(3)
                cachedVersionNumber = name?.substring(0, name.length - 3)?.toInt()!!
            }
            return cachedVersionNumber
        }
}
