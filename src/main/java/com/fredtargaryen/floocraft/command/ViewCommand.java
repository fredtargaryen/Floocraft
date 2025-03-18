package com.fredtargaryen.floocraft.command;

import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ViewCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("viewfireplace")
                .requires(e -> e.hasPermission(2))
                .then(Commands.argument("Dimension", DimensionArgument.dimension())
                        .then(Commands.argument("Fireplace search query (\"\" for all fireplaces)", StringArgumentType.string())
                                .executes(e -> execute(
                                        e.getSource(),
                                        DimensionArgument.getDimension(e, "Dimension"),
                                        StringArgumentType.getString(e, "Fireplace search query (\"\" for all fireplaces)")
                                )))));
    }

    private static int execute(CommandSourceStack source, ServerLevel level, String query) {
        ConcurrentHashMap<String, int[]> placeList = FloocraftLevelData.getForLevel(level).placeList;
        Iterator<String> keyIterator = placeList.keySet().iterator();
        boolean placesFound = false;
        while (keyIterator.hasNext()) {
            String s = keyIterator.next();
            if (s.startsWith(query)) {
                placesFound = true;
                int[] coords = placeList.get(s);
                source.sendSuccess(() -> Component.literal(String.format("\"%s\" exists at (%d, %d, %d)", s, coords[0], coords[1], coords[2])), false);
            }
        }
        if (!placesFound) {
            source.sendFailure(Component.literal(String.format("No places were found with names beginning with \"%s\".", query)));
        }
        return 0;
    }
}
