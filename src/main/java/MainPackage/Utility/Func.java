package MainPackage.Utility;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.collision.CollisionFlags;
import org.powbot.mobile.script.ScriptManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static MainPackage.Utility.GV.bannedTiles;

public class Func {

    public static boolean atGE() {
        return Areas.GRAND_EXCHANGE.contains(Players.local());
    }
    public static boolean atLumbridge() {
        return Areas.LUMBRIDGE.contains(Players.local());
    }
    public static boolean atBloodMoonArea() {
        return Areas.BLOOD_MOON_AREA.contains(Players.local());
    }
    public static boolean atEarthCavern() {
        return Areas.EARTH_CAVERN.contains(Players.local());
    }
    public static boolean atBossChamber() {
        return Areas.BOSS_CHAMBER.contains(Players.local());
    }
    public static boolean atAncientPrison() {
        return Areas.ANCIENT_PRISON.contains(Players.local());
    }
    public static boolean atLunarChestArea() {return Areas.CHEST_AREA.contains(Players.local());}
    public static boolean atBloodMoonLobby() {return Areas.BLOODMOON_LOBBY.contains(Players.local());}
    public static boolean atStreamCavern() {return Areas.STREAM_CAVERN.contains(Players.local());}








    public static void zoomOut(){
        if (Game.tab() != Game.Tab.SETTINGS) {
            if (Game.tab(Game.Tab.SETTINGS)) {
                Condition.wait( () -> Game.tab() == Game.Tab.SETTINGS , 200, 50);
            }
        }
        Component slider = Components.stream().action("Restore Default Zoom").first();
        if (!slider.visible()) {
            Component displayWid = Widgets.widget(116).component(112);
            if (displayWid.valid() && displayWid.click()) {
                Condition.wait(
                        () -> Components.stream().action("Restore Default Zoom").first().visible()
                        , 200, 50);
            }
        } else {
            Camera.moveZoomSlider(1.0);
        }
    }

    public static Tile getNextSafespotTile(Tile playerTile) {
        // Ensure to use your predefined clockwise safespot tiles
        List<Tile> predefinedSafespotTiles = GV.nextSafeSpotTiles;

        // Find the current index of the player's tile in the list
        int currentIndex = predefinedSafespotTiles.indexOf(playerTile);

        // Check if the player's tile is in the list
        if (currentIndex == -1) {
            System.out.println("Player's current tile not found in predefined safespot tiles.");
            return null;
        }

        // Get the next index clockwise (wrap around if at the end of the list)
        int nextIndex = (currentIndex + 1) % predefinedSafespotTiles.size();

        // Return the next safespot tile
        return predefinedSafespotTiles.get(nextIndex);
    }

    public static boolean collectAllToBank() {
        int emptySlots = Inventory.emptySlotCount();
        if(Widgets.component(465,6,0).interact("Collect to bank")){
            Condition.wait(() -> emptySlots != Inventory.emptySlotCount(), Random.nextInt(150, 200), 4);
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean collectAllToInventory() {
        int emptySlots = Inventory.emptySlotCount();
        if(Widgets.component(465,6,0).interact("Collect to inventory")){
            Condition.wait(() -> emptySlots != Inventory.emptySlotCount(), Random.nextInt(150, 200), 4);
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean slotsFinishedBuying(){
        return GrandExchange.allSlots().stream().anyMatch(GeSlot::isFinished);
    }


    public static boolean slotsAvailable(){
        return GrandExchange.allSlots().stream().anyMatch(GeSlot::isAvailable);
    }


    public static boolean wearingFullOutfit(){
        return Equipment.itemAt(Equipment.Slot.TORSO).name().contentEquals("")
                && Equipment.itemAt(Equipment.Slot.LEGS).name().contentEquals("")
                && Equipment.itemAt(Equipment.Slot.FEET).name().contentEquals("")
                && Equipment.itemAt(Equipment.Slot.HEAD).name().contentEquals("")
                && Equipment.itemAt(Equipment.Slot.HANDS).name().contentEquals("");
    }

    public static void checkForRareItems() {
        // List of rare items to check for
        List<String> rareItems = Arrays.asList(
                "Dual macuahuitl",
                "Blood moon helm",
                "Blood moon chestplate",
                "Blood moon tassets"
        );

        // Check if any of the rare items are in the inventory
        boolean hasRareItem = Inventory.stream()
                .anyMatch(item -> rareItems.contains(item.name()));

        if (hasRareItem) {
            System.out.println("Rare item found in inventory. Stopping script.");
            ScriptManager.INSTANCE.stop(); // Stops the script
        }
    }

    public static void dropUnwantedItems() {
        // List of unwanted item names (cleaned up from the OSRS wiki drop table)
        List<String> unwantedItems = Arrays.asList(
                "Atlatl dart",
                "Wyrmling bones",
                "Swamp tar",
                "Water orb",
                "Supercompost",
                "Soft clay",
                "Grimy harralander",
                "Grimy irit leaf",
                "Maple seed",
                "Yew seed",
                "Sun-kissed bones"
        );

        // Loop through inventory and drop any unwanted items
        Inventory.stream()
                .filter(item -> unwantedItems.contains(item.name()))
                .forEach(item -> {
                    System.out.println("Dropping unwanted item: " + item.name());
                    if (item.interact("Drop")) {
                        Condition.wait(() -> !item.valid(), 150, 10); // Wait until the item is no longer in the inventory
                    }
                });
    }

    public static void checkBarrowsEquipment() {
        // Check head, chest, and legs slots for items containing "25" in their name
        boolean hasItemWith25 = Equipment.itemAt(Equipment.Slot.HEAD).name().contains("25") ||
                Equipment.itemAt(Equipment.Slot.TORSO).name().contains("25") ||
                Equipment.itemAt(Equipment.Slot.LEGS).name().contains("25");

        if (GV.RESTOCK_SUPPLIES && hasItemWith25) {
                GV.repairBarrowsArmour = true;
                System.out.println("Item with '25' found in equipment and restock supplies is true. Walking to closest bank.");
        }
    }

    public static boolean deathAllItemsRetrieved(){
        String[] wearableItems = {"top", "legs", "shoes", "gloves", "mask"};
        return Inventory.stream().name(wearableItems[0]).isNotEmpty()
                && Inventory.stream().name(wearableItems[1]).isNotEmpty()
                && Inventory.stream().name(wearableItems[2]).isNotEmpty()
                && Inventory.stream().name(wearableItems[3]).isNotEmpty()
                && Inventory.stream().name(wearableItems[4]).isNotEmpty();
    }

    public static int deathsItemRetrivalAmount() {
        String itemsAmount = Widgets.component(669, 1, 1).text();

        // Check if the widget text is null or empty to prevent issues
        if (itemsAmount == null || itemsAmount.isEmpty()) {
            return 0; // Return a default value
        }

        // Extract the max items value
        String maxItems = itemsAmount.substring(0, itemsAmount.indexOf("/"));
        maxItems = maxItems.substring(maxItems.indexOf("(") + 1);

        return Integer.parseInt(maxItems);
    }


    public static boolean bloodMoonInvalid() {
        Npc bloodMoon = Npcs.stream().name("Blood Moon").within(Areas.BLOOD_MOON_AREA).nearest().first();

        return !bloodMoon.valid() // Blood Moon NPC is not valid
                || bloodMoon.animation() == 11000 // Blood Moon animation is 11000
                || bloodMoon.animation() == 11003; // Blood Moon animation is 11003
    }

    // Checks if you have all the muling items from the bank in your inventory
    public static boolean gotAllItemsForTrade(){
        return Inventory.stream().name("").count(true) >= GV.MAIN_ITEM_AMOUNT;
    }


    public static void checkAndMove() {
        // Get the player's current tile
        Tile playerTile = Players.local().tile();

        // Iterate over all objects with the specified ID
        Objects.stream().id(51046).forEach(obj -> {
            Tile tile = obj.tile(); // Get the tile of the object
            if (!GV.bloodRainTiles.contains(tile)) { // Avoid duplicates
                GV.bloodRainTiles.add(tile);
            }
        });

        // Check if the player's tile contains the object with ID 51046
        if (GV.bloodRainTiles.contains(playerTile)) {
            System.out.println("Player is on a dangerous tile: " + playerTile);

            // Find a safe tile nearby
            Tile safeTile = findSafeTile();

            // Move to the safe tile if found and the player is not already moving
            if (safeTile != null) {
                if(Movement.step(safeTile)){
                    System.out.println("Moved to safe tile: " + safeTile);
                    // Wait until the player starts moving or the condition times out
                    Condition.wait(() -> Players.local().inMotion(), 100, 10);
                }
            } else {
                System.out.println("Failed to move to safe tile: " + safeTile);
                // Optionally retry or log for debugging
            }
        } else {
            System.out.println("Player is on a safe tile.");
        }
    }


    public static Tile findSafeTile() {
        Tile playerTile = Players.local().tile();
        Tile centerTile = new Tile(1392, 9632, 0);

        // Create a list to hold candidate tiles
        List<Tile> candidateTiles = new ArrayList<>();

        // Iterate through a 3x3 area around the player
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Tile candidateTile = playerTile.derive(x, y);

                // Check if the candidate tile is safe and walkable
                boolean isSafe = Objects.stream()
                        .at(candidateTile) // Only check objects on the candidate tile
                        .id(51046) // Dangerous object ID
                        .isEmpty(); // If empty, it's safe


                if (isSafe) {
                    candidateTiles.add(candidateTile);
                }
            }
        }

        // Prioritize tiles closer to the center
        return candidateTiles.stream()
                .min(Comparator.comparingDouble(tile -> tile.distanceTo(centerTile)))
                .orElse(null); // Return the closest safe and walkable tile to the center, or null if none found
    }

    public static List<Tile> getSafeSpotTiles() {
        // Get the safespot NPC by ID 13015
        Npc safespotNpc = Npcs.stream().id(13015).within(Areas.BLOOD_MOON_AREA).nearest().first();

        GV.safespotTiles.clear(); // Clear previous safespot tiles

        if (safespotNpc.valid() && safespotNpc.tile() != null) {
            Tile trueTile = safespotNpc.tile(); // Get the true (top-right) tile

            // Add the 4 tiles around the safespot NPC
            GV.safespotTiles.add(trueTile); // Top-right
            GV.safespotTiles.add(new Tile(trueTile.x() - 1, trueTile.y())); // Left
            GV.safespotTiles.add(new Tile(trueTile.x(), trueTile.y() - 1)); // Down
            GV.safespotTiles.add(new Tile(trueTile.x() - 1, trueTile.y() - 1)); // Bottom-left

            GV.safespotTilesChanged=false;

            // Debugging: Log the tiles
            System.out.println("Get safe spot tiles function active");

        }

        return GV.safespotTiles; // Return the populated list
    }

    public static Tile findClosestJaguarTileToSafespot() {
        Tile closestJaguarTile = null;
        double closestDistance = Double.MAX_VALUE;

        // Ensure safespot tiles are up-to-date
        if (!GV.safespotTiles.isEmpty()) {
            for (Tile safespotTile : GV.safespotTiles) {
                if (safespotTile != null) { // Check if the safespot tile is valid
                    // Iterate over predefined Jaguar tiles
                    for (Tile jaguarTile : GV.jaguarTiles) {
                        double distance = safespotTile.distanceTo(jaguarTile);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestJaguarTile = jaguarTile;
                        }
                    }
                }
            }
        }

        if (closestJaguarTile != null) {
            System.out.println("Closest Jaguar Tile: " + closestJaguarTile);
        } else {
            System.out.println("No valid safespot or jaguar tiles found.");
        }

        return closestJaguarTile;
    }

    public static List<Tile> updateBloodRainTiles() {
        // Iterate over all objects with the blood rain ID
        Objects.stream().id(51046).within(Areas.BLOOD_MOON_AREA).forEach(obj -> {
            Tile tile = obj.tile(); // Get the tile of the object
            if (!GV.bloodRainTiles.contains(tile)) { // Avoid duplicates
                GV.bloodRainTiles.add(tile);
            }
        });

        // Debugging: Log the tiles
        System.out.println("Updated Blood Rain Tiles: " + GV.bloodRainTiles);

        return GV.bloodRainTiles; // Return the updated list
    }

    public static int prayerPoints() {
        String text = Widgets.component(897, 16).text();
        if (text == null || text.isEmpty()) {
            // Return a default value, such as 0, if the text is empty or null
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // Handle the exception, log it for debugging, and return a default value
            System.err.println("[ERROR] Failed to parse prayer points: " + text);
            return 0;
        }
    }


    public static void drinkMoonlightPotion(){
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
                Condition.wait(() -> Func.prayerPoints() != oldPrayerPoints, 100, 8);
            }
        }
    }

    public static void getTotalSupplies() {
        AtomicInteger totalDoses = new AtomicInteger();

        // Stream through inventory and filter Moonlight potion items
        Inventory.stream()
                .nameContains("Moonlight potion")
                .forEach(potion -> {
                    String name = potion.name();
                    String dose = name.replaceAll("[^0-9]", ""); // Extract the numeric part
                    totalDoses.addAndGet(dose.isEmpty() ? 0 : Integer.parseInt(dose)); // Add the doses
                });

        System.out.println("Total doses of Moonlight potion: " + totalDoses.get());

        // Check if the total doses are below 4 or Cooked bream is less than 5
        if (Inventory.stream().name("Cooked bream").count() < 3 || totalDoses.get() < 3) {
            GV.RESTOCK_SUPPLIES = true;
        }
    }

    public static boolean specialAttackEnabled() {
        int textureId = Widgets.component(897, 33).textureId();

        if (textureId == 1608) {
            System.out.println("Special attack is enabled.");
            return true;
        } else if (textureId == 1607) {
            System.out.println("Special attack is disabled.");
            return false;
        } else {
            System.out.println("Unable to determine special attack status.");
            return false;
        }
    }

    public static boolean stepUnder() {
        // Check if all required conditions are true
        return GV.bloodAttackAnimation && GV.siphonPorjectileDetected;
    }

}
