package com.example.durabilitymod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "durabilitymod")
public class DurabilityChecker {

    // Переменная, чтобы сообщение не спамило в чат каждую миллисекунду
    private static boolean hasWarned = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        // Проверяем инструмент только на стороне сервера, когда тик игрока завершается
        if (event.phase == TickEvent.Phase.END && !player.level().isClientSide()) {
            
            // Берем предмет, который ты прямо сейчас держишь в главной руке
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);

            // Проверяем, есть ли у этого предмета прочность (чтобы не проверять блоки земли или еду)
            if (itemInHand.isDamageableItem()) {
                
                // Вычисляем оставшуюся прочность: максимальная минус текущая поломка
                int currentDurability = itemInHand.getMaxDamage() - itemInHand.getDamageValue();

                // Если прочности осталось МЕНЬШЕ 50 единиц
                if (currentDurability < 50) {
                    if (!hasWarned) {
                        // Отправляем тебе в чат ярко-красное сообщение
                        player.sendSystemMessage(Component.literal(
                            "§c[ВНИМАНИЕ] Прочность твоего инструмента меньше 50! Он скоро сломается!"
                        ));
                        hasWarned = true; // Запоминаем, что уже предупредили
                    }
                } else {
                    // Если ты взял целую кирку или починил старую, сбрасываем триггер
                    hasWarned = false; 
                }
            } else {
                // Если в руке ничего нет или предмет не ломается — сбрасываем триггер
                hasWarned = false;
            }
        }
    }
}
