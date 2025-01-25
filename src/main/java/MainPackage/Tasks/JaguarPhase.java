package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.Point;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.item.InventoryItemStream;
import org.powbot.mobile.SettingsManager;
import org.powbot.mobile.ToggleId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JaguarPhase extends Task {

    MainClass main;

    public JaguarPhase(MainClass main) {
        super();
        super.name = "JaguarPhase";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Npcs.stream().name("Blood jaguar").within(Areas.BLOOD_MOON_AREA).nearest().first().valid()
                && Npcs.stream().name("Blood jaguar").within(Areas.BLOOD_MOON_AREA).nearest().first().animation() != 10960
                && Npcs.stream().id(13015).within(Areas.BLOOD_MOON_AREA).nearest().first().valid()
                && Func.atBloodMoonArea()
                && !GV.BLOOD_MOON_DEAD
                && !Npcs.stream().name("Enraged Blood Moon").within(Areas.BLOOD_MOON_AREA).nearest().first().valid();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK="Jaguar phase";
        System.out.println("Jaguar phase task is active");
        GV.siphonPorjectileDetected=false;
        GV.stepUnderMessage=false;
        GV.bloodAttackAnimation=false;

        GameObject escapeStairs = Objects.stream().name("Stairs").nearest().first();
        Tile stairsTile = new Tile(1405, 9632, 0);
        InventoryItemStream cookedBream = Inventory.stream().name("Cooked bream");

        if(GV.safespotTilesChanged || GV.safespotTiles.isEmpty()) {
            Func.getSafeSpotTiles(); // Gets the true tile of safe spot NPC and then calculates the rest of the tiles covered manually.
        }

        Tile jaguarTile = Func.findClosestJaguarTileToSafespot(); // Find the closest predefined jaguar tile to the safespot

        Npc bloodJaguar = (jaguarTile != null)
                ? Npcs.stream().name("Blood jaguar").at(jaguarTile).first()
                : null;

        Func.updateBloodRainTiles(); // Iterate over all objects with the blood rain ID


        // Find the safespot tile closest to the Blood Jaguar
        Tile safeTile = GV.safespotTiles.stream()
                .min((tile1, tile2) -> Double.compare(tile1.distanceTo(bloodJaguar.facingTile()), tile2.distanceTo(bloodJaguar.facingTile())))
                .orElse(null);

        // Filter predefined blood rain tiles to only include those within safespot tiles
        List<Tile> validBloodRainTiles = GV.predefinedBloodRainTiles.stream()
                .filter(GV.safespotTiles::contains) // Only tiles that are inside the safespot area
                .collect(Collectors.toList());

        Tile playerTile = Players.local().tile();

        // Find the closest tile among the valid tiles
        Tile stepTile = validBloodRainTiles.stream()
                .min(Comparator.comparingDouble(tile -> tile.distanceTo(playerTile))) // Find the closest one
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
                        Condition.wait(() -> !Func.atBloodMoonArea(), 200, 3);
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
        else if(Func.specialAttackEnabled()){
            Component specialAttackButton = Widgets.component(897, 31);

            // Activate special attack
            if(specialAttackButton.interact("Use")){
                System.out.println("Disabling special attack..");
                Condition.wait(() -> !Func.specialAttackEnabled(), 50, 15);
            }
        }

        else if(GV.dodgeAttack){
            Point stepPoint = stepTile.matrix().mapPoint();
            if(Input.tap(stepPoint)){
                Condition.wait(() -> Players.local().inMotion()
                        || !Npcs.stream().name("Blood jaguar").nearest().first().valid()
                        || !Func.atBloodMoonArea(), 100, 30);
                GV.dodgeAttack=false;
            }
        }

        // Check if the player is already on a safespot tile.
        else if (GV.safespotTiles.contains(Players.local().tile())) {
            System.out.println("Player is already on a safespot tile.");

            // Ensure interaction with Blood Jaguar.
            Actor interacting = Players.local().interacting();
            if (interacting != null && interacting.name().equals("Blood jaguar")) {
                System.out.println("Already interacting with Blood jaguar.");
            } else {
                // Interact with Blood Jaguar.
                bloodJaguar.bounds(-10, 10, -10, 10, -10, 10);
                if (bloodJaguar.valid() && bloodJaguar.interact("Attack")) {
                    System.out.println("Attacking Blood jaguar.");


                    Condition.wait(() -> Players.local().interacting() != null
                            && Players.local().interacting().name().equals("Blood jaguar")
                            && Players.local().inCombat()
                            || !bloodJaguar.valid()
                            || GV.dodgeAttack
                            || !Func.atBloodMoonArea(), 100, 20);
                } else {
                    System.out.println("Failed to attack Blood jaguar.");
                }
            }
        } else {
            // Move to the closest safespot tile.
            if (safeTile != null && Movement.step(safeTile)) {
                System.out.println("Moving to jaguar facing tile");
                // Condition wait for arrival at safespot while checking and eating food if health is below 70%
                Condition.wait(() -> {
                    if (Players.local().healthPercent()<60 && Game.tab(Game.Tab.INVENTORY)) {
                        if (Inventory.stream().name("Cooked bream").first().interact("Eat")) {
                            Condition.wait(() -> Players.local().healthPercent()>60, 50, 12);
                            System.out.println("Health percent less than 70%, eating bream.");
                        }
                    }
                    // Continue waiting until at safespot tile or dodge attack triggers
                    return GV.safespotTiles.contains(Players.local().tile()) || GV.dodgeAttack || !Func.atBloodMoonArea();
                }, 200, 15);

            } else {
                System.out.println("Failed to move to safespot tile: " + jaguarTile);
            }
        }

    }
}
