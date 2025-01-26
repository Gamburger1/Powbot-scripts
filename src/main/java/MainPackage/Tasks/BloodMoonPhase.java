package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.*;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.item.InventoryItemStream;
import java.util.Comparator;



public class BloodMoonPhase extends Task {

    int safespotNpcID = 13015;
    int jaguarDeathAnimation = 10960;

    MainClass main;

    public BloodMoonPhase(MainClass main) {
        super();
        super.name = "BloodMoonPhase";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return (!Npcs.stream().name("Blood jaguar").within(Areas.BLOOD_MOON_AREA).nearest().first().valid()
                || Npcs.stream().name("Blood jaguar").within(Areas.BLOOD_MOON_AREA).nearest().first().animation() == jaguarDeathAnimation)
                && Npcs.stream().id(safespotNpcID).within(Areas.BLOOD_MOON_AREA).nearest().first().valid()
                && Func.atBloodMoonArea()
                && Npcs.stream().name("Blood Moon").within(Areas.BLOOD_MOON_AREA).nearest().first().valid()
                && !GV.BLOOD_MOON_DEAD;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "Blood moon phase";
        System.out.println("Blood moon phase task is active");

        GV.bloodRainTiles.clear();
        GV.tickToDodge=7;
        GV.dodgeAttack=false;
        GV.tickCounter=0;

        Tile centerTile = new Tile(1392, 9632, 0);
        Tile stairsTile = new Tile(1405, 9632, 0);

        InventoryItemStream specWeapon = Inventory.stream().name("Dragon dagger(p++)");
        InventoryItemStream mainWeapon = Inventory.stream().name("Abyssal whip");
        InventoryItemStream cookedBream = Inventory.stream().name("Cooked bream");

        Npc bloodMoon = Npcs.stream().name("Blood Moon").within(Areas.BLOOD_MOON_AREA).nearest().first();
        GameObject escapeStairs = Objects.stream().name("Stairs").nearest().first();

        // Check if special attack percentage is below 25
        if(Combat.specialPercentage()<25){
            GV.useSpecialAttack=false;
        }

        // Update safe spot tiles if changed or empty
        if(GV.safespotTilesChanged || GV.safespotTiles.isEmpty()) {
            Func.getSafeSpotTiles();
        }

        // Find the closest safe spot tile to the center (Blood Moon tile) and closest step-under tile to the player
        Tile safeTile = GV.safespotTiles.stream()
                .min(Comparator.comparingDouble(tile -> tile.distanceTo(centerTile)))
                .orElse(null);

        // Find the closest step under tile to player local
        Tile closestStepUnderTile = GV.stepUnderTiles.stream()
                .min(Comparator.comparingDouble(tile -> tile.distanceTo(Players.local().tile())))
                .orElse(null);


        // Handle player escape when food is empty
        if (cookedBream.isEmpty()) {
            System.out.println("No food available. Attempting to escape.");
            Game.closeOpenTab();
            if (escapeStairs.inViewport()) {
                if (escapeStairs.interact("Quick-escape")) {
                    if (Condition.wait(() -> Players.local().inMotion(), 100, 3)) {
                        System.out.println("Successfully interacted with escape stairs.");
                        GV.RESTOCK_SUPPLIES = true;
                        Condition.wait(() -> Func.atBossChamber(), 450, 4);
                    }
                } else {
                    System.out.println("Failed to interact with escape stairs.");
                }
            } else {
                Point stairsTilePoint = stairsTile.matrix().mapPoint();
                if (Input.tap(stairsTilePoint)) {
                    System.out.println("Successfully stepped towards escape stairs.");
                    if (Condition.wait(() -> Players.local().inMotion(), 100, 3)) {
                        System.out.println("Player in motion, waiting for stairs to get in view.");
                        Condition.wait(() -> escapeStairs.inViewport(), 150, 10);
                    }
                } else {
                    System.out.println("Failed to step towards escape stairs.");
                }
            }
        }

        // Move to the safe spot tile
        else if (!Players.local().tile().equals(safeTile)) {
            Point safeTilePoint = safeTile.matrix().mapPoint();
            if (Input.tap(safeTilePoint)) {
                System.out.println("Stepping to safetile");
                if(Condition.wait(() -> Players.local().inMotion(), 100, 4)){
                    System.out.println("Moving to safespot tile: " + safeTile);
                    Condition.wait(() -> Players.local().tile().equals(safeTile) || GV.safespotTilesChanged, 100, 50);
                }
            } else{
                System.out.println("Failed to Step to safetile");
            }
        }
        // Handle special attacks and weapon switching
        else if (GV.useSpecialAttack && Combat.specialPercentage()>=25 && !Func.specialAttackEnabled()) {

            System.out.println("Special attack not active, enabling..");
            Component specialAttackButton = Widgets.component(897, 31);
            int specialAttackPercent = Combat.specialPercentage();

            // Activate special attack
            if(specialAttackButton.interact("Use")){
                System.out.println("Interacting with special attack widget..");
                Condition.wait(() -> Func.specialAttackEnabled()
                        || Func.stepUnder()
                        || GV.safespotTilesChanged, 50, 6);
            } else{
                System.out.println("Failed to interact with special attack widget..");
            }
        }
        else if(!GV.useSpecialAttack && !Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(mainWeapon.first().name())){
            System.out.println("Special attack used up, wielding main weapon..");
            if(mainWeapon.first().interact("Wield")){
                Condition.wait(() -> Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(mainWeapon.first().name())
                        || Func.stepUnder()
                        || GV.safespotTilesChanged, 100, 10);
            } else{
                System.out.println("Failed to wield main weapon..");
            }
        }
        // Health management
        else if(Players.local().healthPercent()<40 && Game.tab(Game.Tab.INVENTORY)){
            System.out.println("Health precent less than 90%, eating bream.");
            if(Inventory.stream().name("Cooked bream").first().interact("Eat")){
                System.out.println("Successfully interacted with Cooked bream..");
                Condition.wait(() -> Players.local().healthPercent()>40 || Func.stepUnder() || GV.safespotTilesChanged, 150, 5);
            } else{
                System.out.println("Failed to interact with Cooked bream.");
            }
        }
        // Handle step-under mechanics
        else if (Func.stepUnder()) {
            if(Players.local().tile().equals(safeTile) && Players.local().tile().equals(GV.porjectileStartTile)) {
                Point stepPoint = closestStepUnderTile.matrix().mapPoint();
                if (Input.tap(stepPoint)) {
                    System.out.println("Stepping to step under tile = " + closestStepUnderTile);
                    Condition.wait(() -> Players.local().inMotion(), 100, 6);
                    if (bloodMoon.valid() && bloodMoon.interactionType(ModelInteractionType.BoundingModel).interact("Attack")) {
                        System.out.println("Attacking Blood Moon after stepping under.");
                        // Wait until the player is interacting with the Blood Moon
                        Condition.wait(() -> Players.local().interacting().name().equals("Blood moon")
                                || GV.safespotTilesChanged
                                || GV.BLOOD_MOON_DEAD, 100, 12);
                    }
                } else {
                    System.out.println("Failed to step to stepUnder tile: " + closestStepUnderTile);
                }
            }
            GV.stepUnderMessage=false;
            GV.siphonPorjectileDetected=false;
            GV.bloodAttackAnimation=false;
        }
        // Attack logic
        else if (Players.local().tile().equals(safeTile) && !Players.local().interacting().name().equals("Blood Moon") && !Func.stepUnder()) {

            if (bloodMoon.valid() && bloodMoon.interactionType(ModelInteractionType.BoundingModel).interact("Attack")) {
                System.out.println("Attacking Blood Moon.");
                Condition.wait(() -> Players.local().interacting().name().equals("Blood moon")
                        || GV.safespotTilesChanged
                        || Func.stepUnder()
                        || GV.BLOOD_MOON_DEAD, 100, 24);
            } else {
                System.out.println("Failed to interact with Blood Moon.");
            }
        }

        // Handle prayer management
        else if(Func.prayerPoints() < 45 && Game.tab(Game.Tab.INVENTORY)){
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
                    System.out.println("Drinking potion..");
                    Condition.wait(() -> Func.prayerPoints() != oldPrayerPoints
                            || GV.safespotTilesChanged
                            || Func.stepUnder(), 100, 10);
                } else{
                    System.out.println("failed to drink potion..");
                }
            }
        }
        // Handle prayer activation
        else if (!Prayer.prayersActive() && Func.prayerPoints() > 0) {
            System.out.println("Activating Quick Prayer...");
            Component quickPrayer = Widgets.component(897, 15);
            if(quickPrayer.interact("Activate")){
                Condition.wait(() -> Prayer.prayersActive()
                        || GV.safespotTilesChanged
                        || Func.stepUnder(), 100, 10);
            } else {
                System.out.println("Failed to activate Quick Prayer.");
            }
        }

    }
}
