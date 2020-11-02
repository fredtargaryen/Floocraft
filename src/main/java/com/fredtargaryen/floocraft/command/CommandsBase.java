package com.fredtargaryen.floocraft.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class CommandsBase {
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        ViewCommand.register(dispatcher);
        RemoveCommand.register(dispatcher);
    }
}
