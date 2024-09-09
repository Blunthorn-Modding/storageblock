package net.wouterb.mixin;

import com.tom.storagemod.tile.CraftingTerminalBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
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

@SuppressWarnings("UnreachableCode")
@Mixin(CraftingTerminalBlockEntity.class)
public class CraftingTerminalBlockEntityMixin {

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void craft(PlayerEntity player, CallbackInfo ci) {
        System.out.println("CRAFT!");
        World world = player.getWorld();
        CraftingTerminalBlockEntity craftingTerminalBlockEntity = (CraftingTerminalBlockEntity) (Object) this;
        RecipeInputInventory craftingInventory = craftingTerminalBlockEntity.getCraftingInv();

        if (world.isClient) return;

        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);

        if (optional.isEmpty()) return;

        ItemStack output = optional.get().getOutput(world.getRegistryManager());

        ItemActionContext itemActionContext = new ItemActionContext(world, player, output, LockType.CRAFTING_RECIPE);
        ActionResult actionResult = ObjectCraftedEvent.emit(itemActionContext);

        if (actionResult == ActionResult.FAIL) {
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
            ci.cancel();
        }

    }

//    @Inject(method = "onCraftingMatrixChanged", at = @At("HEAD"), cancellable = true, remap = false)
//    protected void onCraftingMatrixChanged(CallbackInfo ci) {
//        System.out.println("MATRIX CHANGED");
////        ci.cancel();
//    }
}
