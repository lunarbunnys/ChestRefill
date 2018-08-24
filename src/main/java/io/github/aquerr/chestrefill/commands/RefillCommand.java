package io.github.aquerr.chestrefill.commands;

import io.github.aquerr.chestrefill.PluginInfo;
import io.github.aquerr.chestrefill.entities.RefillableContainer;
import io.github.aquerr.chestrefill.managers.ContainerManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

/**
 * Created by Aquerr on 2018-06-24.
 */
public class RefillCommand implements CommandExecutor
{
    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        Optional<String> optionalChestName = args.getOne(Text.of("箱子名称"));

        if (optionalChestName.isPresent())
        {
            String chestName = optionalChestName.get();

            List<RefillableContainer> containerList = ContainerManager.getRefillableContainers();

            for (RefillableContainer refillableContainer : containerList)
            {
                if (refillableContainer.getName() != null && refillableContainer.getName().equals(chestName))
                {
                    boolean didSucceed = ContainerManager.refillContainer(refillableContainer.getContainerLocation());
                    if(didSucceed)
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "成功重装指定箱子!"));
                    else
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "无法重装指定的箱子!"));
                    break;
                }
            }
        }

        return CommandResult.success();
    }
}
