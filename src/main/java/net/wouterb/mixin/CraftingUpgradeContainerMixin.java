package net.wouterb.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.event.ObjectCraftedEvent;
import net.wouterb.blunthornapi.api.permission.LockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingUpgradeContainer.class)
public class CraftingUpgradeContainerMixin {

    @Inject(method = "updateCraftingResult", at = @At("HEAD"), cancellable = true)
    public void updateCraftingResult(World world, PlayerEntity player, RecipeInputInventory inventory, CraftingResultInventory inventoryResult, CraftingResultSlot craftingResultSlot, CallbackInfo ci) {
        if (world.isClient) return;

        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, world);

        if (optional.isEmpty()) return;

        ItemStack output = optional.get().getOutput(world.getRegistryManager());

        ItemActionContext itemActionContext = new ItemActionContext(world, player, output, LockType.CRAFTING_RECIPE);
        ActionResult actionResult = ObjectCraftedEvent.emit(itemActionContext);

        if (actionResult == ActionResult.FAIL)
            ci.cancel();
    }
}
