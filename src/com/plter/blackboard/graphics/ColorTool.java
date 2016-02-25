package com.plter.blackboard.graphics;

import javafx.scene.paint.Color;

/**
 * Created by plter on 2/25/16.
 */
public class ColorTool {


    public static Color cloneColor(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
    }
}
