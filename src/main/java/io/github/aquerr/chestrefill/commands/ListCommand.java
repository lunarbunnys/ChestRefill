package io.github.aquerr.chestrefill.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import io.github.aquerr.chestrefill.PluginInfo;
import io.github.aquerr.chestrefill.entities.RefillableContainer;
import io.github.aquerr.chestrefill.managers.ContainerManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Aquerr & vvozny on 2018-02-09.
 */

public class ListCommand implements CommandExecutor
{
    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        List<Text> helpList = Lists.newArrayList();

        for(RefillableContainer refillableContainer : ContainerManager.getRefillableContainers())
        {
            Text.Builder itemsToShow = Text.builder();

            itemsToShow.append(Text.of(TextColors.GREEN, "箱子名称: ", TextColors.YELLOW, refillableContainer.getName() + "\n"));
            itemsToShow.append(Text.of(TextColors.GREEN, "箱子内的物品: " + "\n"));
            refillableContainer.getItems().forEach(x-> itemsToShow.append(Text.of(TextColors.YELLOW, x.getItem().getType().getName(), TextColors.RESET, " x" + x.getItem().getQuantity() + "\n")));
            itemsToShow.append(Text.of("\n", TextColors.GREEN, "一次一件物品: ", TextColors.WHITE,  refillableContainer.isOneItemAtTime(), "\n"));
            itemsToShow.append(Text.of(TextColors.GREEN, "重放已存在的物品: ", TextColors.WHITE, refillableContainer.shouldReplaceExistingItems(), "\n"));
            itemsToShow.append(Text.of(TextColors.GREEN, "如果无物品就隐藏: ", TextColors.WHITE, refillableContainer.shouldBeHiddenIfNoItems(), "\n"));
            itemsToShow.append(Text.of(TextColors.GREEN, "隐藏的方块: ", TextColors.WHITE, refillableContainer.getHidingBlock(), "\n"));
            itemsToShow.append(Text.of("\n", TextColors.BLUE, TextStyles.BOLD, "箱子冷却时间: ", refillableContainer.getRestoreTime(),"s\n"));
            itemsToShow.append(Text.of("\n", TextColors.RED, TextStyles.ITALIC, "点我传送..."));

//            Text chestText = Text.builder()
//                    .append(Text.of(TextColors.DARK_GREEN, "Container at ", TextColors.YELLOW, refillableContainer.getContainerLocation().getBlockPosition().toString()))
//                    .onHover(TextActions.showText(itemsToShow.build()))
//                    .onClick(TextActions.executeCallback(teleportToChest(source, refillableContainer.getContainerLocation().getBlockPosition())))
//                    .build();

            Text.Builder chestName = Text.builder();
            if(refillableContainer.getName().equals(""))
                chestName.append(Text.of("未命名的箱子"));
            else
                chestName.append(Text.of("箱子 ", TextColors.YELLOW, refillableContainer.getName()));

            Text chestText = Text.builder()
                    .append(Text.of(TextColors.YELLOW, " - ", TextColors.DARK_GREEN, chestName.build(), " 位于 ", TextColors.YELLOW, refillableContainer.getContainerLocation().getBlockPosition().toString()))
                    .onHover(TextActions.showText(itemsToShow.build()))
                    .onClick(TextActions.executeCallback(teleportToChest(source, refillableContainer.getContainerLocation().getBlockPosition())))
                    .build();

            helpList.add(chestText);
        }

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder paginationBuilder = paginationService.builder().title(Text.of(TextColors.GOLD, "重装箱子列表")).padding(Text.of(TextColors.DARK_GREEN, "-")).contents(helpList).linesPerPage(10);
        paginationBuilder.sendTo(source);


        return CommandResult.success();
    }

    private Consumer<CommandSource> teleportToChest(CommandSource source, Vector3i blockPosition)
    {
        return consumer ->
        {
            //Do we need this check? Only in-game players can click on the chat...
            if (source instanceof Player)
            {
                Player player = (Player)source;

                player.setLocation(new Location<World>(player.getWorld(), blockPosition));
                player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "你已经被传送至选择的箱子!"));
            }
        };
    }
}
