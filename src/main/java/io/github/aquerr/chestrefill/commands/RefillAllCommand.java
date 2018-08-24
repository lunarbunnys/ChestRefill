package io.github.aquerr.chestrefill.commands;

import io.github.aquerr.chestrefill.PluginInfo;
import io.github.aquerr.chestrefill.entities.ContainerLocation;
import io.github.aquerr.chestrefill.managers.ContainerManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class RefillAllCommand implements CommandExecutor
{
    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        boolean didSucceed = true;
        for (ContainerLocation containerLocation : ContainerManager.getContainerLocations())
        {
            boolean refilledContainer = ContainerManager.refillContainer(containerLocation);
            if(!refilledContainer)
                didSucceed = false;
        }

        if(didSucceed)
            source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "成功重装所有箱子!"));
        else source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "一些箱子无法被重装."));

        return CommandResult.success();
    }
}
