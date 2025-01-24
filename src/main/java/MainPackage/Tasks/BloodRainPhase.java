package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.*;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.item.InventoryItemStream;

import java.util.ArrayList;
import java.util.List;


public class BloodRainPhase extends Task {

    int safespotNpcID = 13015;


    MainClass main;

    public BloodRainPhase(MainClass main) {
        super();
        super.name = "BloodRainPhase";
        this.main = main;
    }

    @Override
    public boolean activate() {

        return !Npcs.stream().name("Blood jaguar").within(Areas.BLOOD_MOON_AREA).nearest().first().valid()
                && Func.bloodMoonInvalid()
                && !Npcs.stream().id(safespotNpcID).nearest().within(Areas.BLOOD_MOON_AREA).first().valid()
                && Func.atBloodMoonArea()
                && !GV.BLOOD_MOON_DEAD;
    }

    @Override
    public void execute() {
        GV.CURRENT_TASK = "Bloodrainphase";
        System.out.println("Blood rain phase has started, checking for blood on player tile..");
        int maxHealth = Players.local().maxHealth();
        GV.stepUnderMessage=false;
        GV.siphonPorjectileDetected=false;
        GV.bloodAttackAnimation=false;
        InventoryItemStream mainWeapon = Inventory.stream().name("Abyssal whip");

        if(!GV.safespotTiles.isEmpty()) {
            GV.safespotTiles.clear();
        }

        // Iterate over all objects with the specified ID
        Func.updateBloodRainTiles();

        if(Combat.specialPercentage()<25){
            GV.useSpecialAttack=false;
        }


        else if(!GV.useSpecialAttack && !Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(mainWeapon.first().name())){
            System.out.println("Special attack used up, wielding whip..");
            if(mainWeapon.first().interact("Wield")){
                Condition.wait(() -> Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(mainWeapon.first().name()), 50, 15);
            }
        }

        // Get the player's current tile
        Tile playerTile = Players.local().tile();

        // Check if the player's tile contains the object with ID 51046
        if (GV.bloodRainTiles.contains(playerTile)) {
            System.out.println("Player is on a dangerous tile: " + playerTile);

            // Find a safe tile nearby
            Tile safeTile = Func.findSafeTile();

            // Move to the safe tile if found and the player is not already moving
            if (safeTile != null) {
                Point stepPoint = safeTile.matrix().mapPoint();
                if(Input.tap(stepPoint)){
                    System.out.println("Moved to safe tile: " + safeTile);
                    // Wait until the player starts moving or the condition times out
                    Condition.wait(() -> Players.local().tile().equals(safeTile), 200, 10);
                }
            } else {
                System.out.println("Failed to move to safe tile: " + safeTile);
                // Optionally retry or log for debugging
            }
        }

        if(Players.local().health() <= maxHealth-20 && Game.tab(Game.Tab.INVENTORY)){
            Inventory.stream().name("Cooked bream").first().interact("Eat");
            System.out.println("Health is 16 less than maxhealth, eating bream.");
        }

        if(Func.prayerPoints() <= 50 && Game.tab(Game.Tab.INVENTORY)){
            String moonlightpotion = "";
            int oldPrayerPoints = Func.prayerPoints();
            if (Inventory.stream().name("Moonlight potion(1)").count() > 0) {
                moonlightpotion = "Moonlight potion(1)";
            } else if (Inventory.stream().name("Moonlight potion(2)").count() > 0) {
                moonlightpotion = "Moonlight potion(2)";
            } else if (Inventory.stream().name("Moonlight potion(3)").count() > 0) {
                moonlightpotion = "Moonlight potion(3)";
            } else if (Inventory.stream().name("Moonlight potion(4)").count() > 0) {
                moonlightpotion = "moonlight potion(4)";
            }
            if (!moonlightpotion.equals("")) {
                if(Inventory.stream().name(moonlightpotion).first().interact("Drink")){
                    Condition.wait(() -> Func.prayerPoints() != oldPrayerPoints, 200, 4);
                }
            }
        }

    }
}