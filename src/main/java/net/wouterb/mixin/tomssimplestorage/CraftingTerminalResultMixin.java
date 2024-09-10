package net.wouterb.mixin.tomssimplestorage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.event.ObjectCraftedEvent;
import net.wouterb.blunthornapi.api.permission.LockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(targets = "com.tom.storagemod.gui.CraftingTerminalMenu$Result")
public abstract class CraftingTerminalResultMixin extends CraftingResultSlot{

    public CraftingTerminalResultMixin(PlayerEntity player, RecipeInputInventory input, Inventory inventory, int index, int x, int y) {
        super(player, input, inventory, index, x, y);
    }

    @Inject(method = "onTakeItem", at = @At("HEAD"), cancellable = true)
    public void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        World world = player.getWorld();
        RecipeInputInventory craftingInventory = ((CraftingResultSlotMixin) this).getInput();

        if (world.isClient) return;

        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);

        if (optional.isEmpty()) return;

        ItemStack output = optional.get().getOutput(world.getRegistryManager());

        ItemActionContext itemActionContext = new ItemActionContext(world, player, output, LockType.CRAFTING_RECIPE);
        ActionResult actionResult = ObjectCraftedEvent.emit(itemActionContext);

        if (actionResult == ActionResult.FAIL) {
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
            setStack(output);
            ci.cancel();
        }
    }
}
