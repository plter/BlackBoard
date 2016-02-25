package com.plter.blackboard.graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by plter on 2/25/16.
 */
public class ChalkPathCommands {

    private List<DrawCommand> commands = new ArrayList<>();

    public void clear() {
        commands.clear();
    }

    public void runDrawCommands() {
        for (DrawCommand cmd :
                commands) {
            cmd.execute();
        }
    }

    public void addCommand(DrawCommand cmd) {
        commands.add(cmd);
    }

    public void addCommands(ChalkPathCommands cmds) {
        commands.addAll(cmds.commands);
    }
}
