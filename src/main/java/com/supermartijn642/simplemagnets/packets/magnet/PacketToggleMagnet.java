package com.supermartijn642.simplemagnets.packets.magnet;

import com.supermartijn642.simplemagnets.MagnetItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class PacketToggleMagnet {

    public void encode(FriendlyByteBuf buffer){
    }

    public static PacketToggleMagnet decode(FriendlyByteBuf buffer){
        return new PacketToggleMagnet();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().setPacketHandled(true);

        Player player = contextSupplier.get().getSender();
        if(player != null){
            ItemStack stack = findStack(player);
            if(stack != null && !stack.isEmpty())
                MagnetItem.toggleMagnet(player, stack);
        }
    }

    private static ItemStack findStack(Player player){
        ItemStack stack = findCuriosStack(player);
        if(stack != null && !stack.isEmpty() && stack.getItem() instanceof MagnetItem)
            return stack;

        for(int slot = 0; slot < player.getInventory().getContainerSize(); slot++){
            stack = player.getInventory().getItem(slot);
            if(!stack.isEmpty() && stack.getItem() instanceof MagnetItem)
                return stack;
        }

        return null;
    }

    private static ItemStack findCuriosStack(Player player){ // TODO
        if(ModList.get().isLoaded("curios")){
            ICuriosItemHandler handler = player.getCapability(CuriosCapability.INVENTORY).orElse(null);
            if(handler != null){
                for(IDynamicStackHandler stackHandler : handler.getCurios().values().stream().map(ICurioStacksHandler::getStacks).collect(Collectors.toSet())){
                    for(int slot = 0; slot < stackHandler.getSlots(); slot++){
                        ItemStack stack = stackHandler.getStackInSlot(slot);
                        if(stack.getItem() instanceof MagnetItem)
                            return stack;
                    }
                }
            }
        }

        return null;
    }

}
