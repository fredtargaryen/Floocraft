package com.fredtargaryen.floocraft.command;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ViewCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("viewfireplace")
                .requires(e -> e.hasPermissionLevel(2))
                .then(Commands.argument("Dimension", DimensionArgument.getDimension())
                .then(Commands.argument("Fireplace search query (\"\" for all fireplaces)", StringArgumentType.string())
                .executes(e -> execute(
                        e.getSource(),
                        DimensionArgument.getDimensionArgument(e, "Dimension"),
                        StringArgumentType.getString(e, "Fireplace search query (\"\" for all fireplaces)")
                )))));
    }

    private static int execute(CommandSource source, ServerWorld world, String query) {
        ConcurrentHashMap<String, int[]> placeList = FloocraftWorldData.forWorld(world).placeList;
        Iterator<String> keyIterator = placeList.keySet().iterator();
        boolean placesFound = false;
        while(keyIterator.hasNext())
        {
            String s = keyIterator.next();
            if(s.startsWith(query)) {
                placesFound = true;
                int[] coords = placeList.get(s);
                source.sendFeedback(new StringTextComponent(String.format("\"%s\" exists at (%d, %d, %d)", s, coords[0], coords[1], coords[2])), false);
            }
        }
        if(!placesFound)
        {
            source.sendFeedback(new StringTextComponent(String.format("No places were found with names beginning with \"%s\".", query)), false);
        }
        return 0;
    }
}
