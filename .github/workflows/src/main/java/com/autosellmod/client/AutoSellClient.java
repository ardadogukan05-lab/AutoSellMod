package com.autosellmod.client;

import com.autosellmod.AutoSellMod;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class AutoSellClient implements ClientModInitializer {
    
    private static boolean autoSellEnabled = false;
    private static int sellInterval = 300;
    private static int tickCounter = 0;
    private static final int TICKS_PER_SECOND = 20;

    @Override
    public void onInitializeClient() {
        AutoSellMod.LOGGER.info("AutoSell Client basladi!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!autoSellEnabled || client.player == null) return;
            
            tickCounter++;
            if (tickCounter >= sellInterval * TICKS_PER_SECOND) {
                tickCounter = 0;
                executeSell(client);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            var autosellCommand = ClientCommandManager.literal("autosell")
                
                .then(ClientCommandManager.literal("on")
                    .executes(ctx -> {
                        autoSellEnabled = true;
                        tickCounter = 0;
                        ctx.getSource().sendFeedback(Component.literal(
                            "§a✔ AutoSell AKTIF! §7Her §e" + sellInterval + "s §7satis."
                        ));
                        return 1;
                    })
                )
                
                .then(ClientCommandManager.literal("off")
                    .executes(ctx -> {
                        autoSellEnabled = false;
                        ctx.getSource().sendFeedback(Component.literal(
                            "§c✖ AutoSell DURDURULDU!"
                        ));
                        return 1;
                    })
                )
                
                .then(ClientCommandManager.literal("interval")
                    .then(ClientCommandManager.argument("seconds", IntegerArgumentType.integer(10, 3600))
                        .executes(ctx -> {
                            sellInterval = IntegerArgumentType.getInteger(ctx, "seconds");
                            tickCounter = 0;
                            ctx.getSource().sendFeedback(Component.literal(
                                "§b⏱ Aralik: §e" + sellInterval + " saniye"
                            ));
                            return 1;
                        })
                    )
                )
                
                .then(ClientCommandManager.literal("status")
                    .executes(ctx -> {
                        String status = autoSellEnabled ? "§aAKTIF" : "§cKAPALI";
                        ctx.getSource().sendFeedback(Component.literal(
                            "§6📊 Durum: " + status + " §7| Aralik: §e" + sellInterval + "s"
                        ));
                        return 1;
                    })
                )
                
                .then(ClientCommandManager.literal("now")
                    .executes(ctx -> {
                        executeSell(Minecraft.getInstance());
                        ctx.getSource().sendFeedback(Component.literal(
                            "§6💰 Manuel satis yapildi!"
                        ));
                        return 1;
                    })
                );
            
            dispatcher.register(autosellCommand);
        });
    }

    private void executeSell(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) return;

        boolean hasItems = false;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                hasItems = true;
                break;
            }
        }

        if (hasItems) {
            player.connection.sendCommand("sell all");
            player.displayClientMessage(
                Component.literal("§a[AutoSell] §f✓ Envanter satildi!"),
                false
            );
        }
    }
}
