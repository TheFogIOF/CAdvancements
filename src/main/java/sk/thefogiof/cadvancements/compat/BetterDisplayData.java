package sk.thefogiof.cadvancements.compat;

import com.google.gson.JsonObject;

public class BetterDisplayData {
    public final Integer posX;
    public final Integer posY;
    public final Integer completedIconColor;
    public final Integer uncompletedIconColor;
    public final Integer completedTitleColor;
    public final Integer uncompletedTitleColor;
    public final Integer completedLineColor;
    public final Integer uncompletedLineColor;
    public final Boolean drawDirectLines;
    public final Boolean hideLines;

    public BetterDisplayData(Integer posX, Integer posY,
                             Integer completedIconColor, Integer uncompletedIconColor,
                             Integer completedTitleColor, Integer uncompletedTitleColor,
                             Integer completedLineColor, Integer uncompletedLineColor,
                             Boolean drawDirectLines, Boolean hideLines) {
        this.posX = posX;
        this.posY = posY;
        this.completedIconColor = completedIconColor;
        this.uncompletedIconColor = uncompletedIconColor;
        this.completedTitleColor = completedTitleColor;
        this.uncompletedTitleColor = uncompletedTitleColor;
        this.completedLineColor = completedLineColor;
        this.uncompletedLineColor = uncompletedLineColor;
        this.drawDirectLines = drawDirectLines;
        this.hideLines = hideLines;
    }

    public static BetterDisplayData parse(JsonObject o) {
        return new BetterDisplayData(
                intOrNull(o, "pos_x"),
                intOrNull(o, "pos_y"),
                intOrNull(o, "completed_icon_color"),
                intOrNull(o, "uncompleted_icon_color"),
                intOrNull(o, "completed_title_color"),
                intOrNull(o, "uncompleted_title_color"),
                intOrNull(o, "completed_line_color"),
                intOrNull(o, "uncompleted_line_color"),
                boolOrNull(o, "draw_direct_lines"),
                boolOrNull(o, "hide_lines")
        );
    }

    private static Integer intOrNull(JsonObject o, String k) {
        return o.has(k) && o.get(k).isJsonPrimitive() ? o.get(k).getAsInt() : null;
    }

    private static Boolean boolOrNull(JsonObject o, String k) {
        return o.has(k) && o.get(k).isJsonPrimitive() ? o.get(k).getAsBoolean() : null;
    }
}