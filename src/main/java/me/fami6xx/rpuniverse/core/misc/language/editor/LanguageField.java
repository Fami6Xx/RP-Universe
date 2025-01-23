package me.fami6xx.rpuniverse.core.misc.language.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents one language field in LanguageHandler:
 * - fieldName is the actual Java field name
 * - value is the current text (possibly multi-line)
 * - multiLine = true if and only if the field's *default* value contained '~'
 * - placeholders is a list of placeholders found in the text, e.g. {player}, {jobName}, etc.
 */
public class LanguageField {
    private final Field reflectionField;
    private final String fieldName;

    /**
     * The field's *current* value (from config or fallback).
     */
    private String value;

    /**
     * Whether we allow multi-line editing (split by '~').
     */
    private final boolean multiLine;

    private List<String> placeholders;

    // Regex to find {anything} in the string
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    public LanguageField(Field reflectionField, String fieldName, String currentValue, boolean multiLine) {
        this.reflectionField = reflectionField;
        this.fieldName = fieldName;
        this.multiLine = multiLine;
        // This setter also refreshes placeholders
        setValue(currentValue);
    }

    public Field getReflectionField() {
        return reflectionField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    /**
     * Sets the current value (single or multi-line).
     */
    public void setValue(String newValue) {
        this.value = (newValue == null ? "" : newValue);
        this.placeholders = findPlaceholders(this.value);
    }

    /**
     * Returns either one line (if multiLine=false)
     * or multiple lines (split by '~' if multiLine=true).
     */
    public List<String> getSplitLines() {
        if (!multiLine) {
            // Always treat as single line
            return Collections.singletonList(value);
        }
        // Multi-line scenario
        if (value.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = value.split("~");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part);
        }
        return result;
    }

    /**
     * Sets the value from multiple lines, rejoined by '~' if multiLine = true.
     */
    public void setLines(List<String> lines) {
        if (!multiLine) {
            // If field is not multiLine, we only take the first line
            if (lines.isEmpty()) {
                this.value = "";
            } else {
                this.value = lines.get(0);
            }
        } else {
            // Join them with '~'
            this.value = String.join("~", lines);
        }
        // Recompute placeholders
        this.placeholders = findPlaceholders(this.value);
    }

    public List<String> getPlaceholders() {
        return placeholders;
    }

    private List<String> findPlaceholders(String text) {
        List<String> found = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            // e.g. {player}, {price}, ...
            found.add(matcher.group());
        }
        return found;
    }
}
