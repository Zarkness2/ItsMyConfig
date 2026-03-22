package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderDependancy;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

/**
 * ProgressBar class represents a progress bar with customizable colors and pattern.
 */
public final class ProgressbarPlaceholder extends Placeholder {
    /**
     * Represents the pattern used for rendering a progress bar.
     */
    private final String pattern;
    private final String completedColor;
    private final String progressColor;
    private final String remainingColor;
    private final String completedSymbol;
    private final String progressSymbol;
    private final String remainingSymbol;
    private final int length;

    /**
     * Represents a progress bar with customizable colors and pattern.
     */
    public ProgressbarPlaceholder(
            final String filePath,
            final ConfigurationSection section
    ) {
        super(section, filePath, PlaceholderType.PROGRESS_BAR, PlaceholderDependancy.NONE);
        this.pattern = section.getString("value");
        this.completedColor = section.getString("completed-color");
        this.progressColor = section.getString("progress-color");
        this.remainingColor = section.getString("remaining-color");
        this.completedSymbol = section.getString("completed-symbol", null);
        this.progressSymbol = section.getString("progress-symbol", null);
        this.remainingSymbol = section.getString("remaining-symbol", null);
        this.length = section.getInt("length", this.pattern != null ? this.pattern.length() : 10);
    }

    /**
     * Renders a progress bar based on the given value and max.
     *
     * @param value The current value of the progress bar.
     * @param max The maximum value of the progress bar.
     * @return The rendered progress bar as a string.
     */
    public String render(
            final double value,
            final double max
    ) {
        return buildProgressBar(calculateCompleted(value, max));
    }

    /**
     * Calculates the number of completed elements based on a given value and maximum value.
     * The completed elements are calculated by dividing the value by the maximum value,
     * multiplying the result by the length of the pattern, and rounding it to the nearest integer.
     * The calculated value is then limited to the maximum length of the pattern.
     *
     * @param value the current value
     * @param max   the maximum value
     * @return the number of completed elements
     */
    private int calculateCompleted(final double value, final double max) {
        final double percent = value / max;
        int completed = (int) Math.round(percent * length);
        return Math.min(completed, length);
    }

    /**
     * Builds a progress bar based on the specified completion level.
     *
     * @param completed The level of completion, represented as an integer between 0 and the length of the pattern.
     * @return The progress bar as a string.
     */
    private String buildProgressBar(final int completed) {
        final StringBuilder sb = new StringBuilder();

        if (completedSymbol != null || progressSymbol != null || remainingSymbol != null) {
            // New symbol-based mode
            final String cChar = completedSymbol != null ? completedSymbol : "\u2588";
            final String pChar = progressSymbol != null ? progressSymbol : cChar;
            final String rChar = remainingSymbol != null ? remainingSymbol : "\u2591";

            if (completed > 0) {
                sb.append(completedColor);
                sb.append(cChar.repeat(completed));
            }
            if (completed < length) {
                sb.append(progressColor);
                sb.append(pChar);
                if (completed + 1 < length) {
                    sb.append(remainingColor);
                    sb.append(rChar.repeat(length - completed - 1));
                }
            }
        } else {
            // Original pattern-based mode (backward compatible)
            if (completed != 0) {
                sb.append(completedColor);
                sb.append(pattern, 0, completed);
            }
            if (completed != pattern.length()) {
                sb.append(progressColor);
                sb.append(pattern, completed, completed + 1);
                sb.append(remainingColor);
                sb.append(pattern, completed + 1, pattern.length());
            }
        }
        return sb.toString();
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getResult(
            final OfflinePlayer player,
            final String[] args
    ) {
        if (args.length < 2) {
            return "Invalid args amount";
        }

        try {
            final double value = Double.parseDouble(args[0]);
            final double maxValue = Double.parseDouble(args[1]);
            return ChatColor.translateAlternateColorCodes('&', this.render(value, maxValue));
        } catch (final NumberFormatException ignored) {}
        return "";
    }

}
