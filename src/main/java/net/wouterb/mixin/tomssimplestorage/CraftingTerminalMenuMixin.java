package net.wouterb.mixin.tomssimplestorage;

import com.tom.storagemod.gui.CraftingTerminalMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.event.ObjectCraftedEvent;
import net.wouterb.blunthornapi.api.permission.LockType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CraftingTerminalMenu.class)
public abstract class CraftingTerminalMenuMixin {

    @Final
    @Shadow
    private RecipeInputInventory craftMatrix;

    @Inject(method = "shiftClickItems", at = @At("HEAD"), cancellable = true)
    public void shiftClickItems(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> ci) {

        World world = player.getWorld();

        if (world.isClient) return;

        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftMatrix, world);

        if (optional.isEmpty()) return;

        ItemStack output = optional.get().getOutput(world.getRegistryManager());

        ItemActionContext itemActionContext = new ItemActionContext(world, player, output, LockType.CRAFTING_RECIPE);
        ActionResult actionResult = ObjectCraftedEvent.emit(itemActionContext);

        if (actionResult == ActionResult.FAIL) {
            ci.setReturnValue(ItemStack.EMPTY);
        }

    }
}
