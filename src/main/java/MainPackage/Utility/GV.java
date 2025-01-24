package MainPackage.Utility;

import org.powbot.api.Tile;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Npc;
import org.powbot.api.rt4.Npcs;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class GV {


    public static String CURRENT_TASK;

    public static int MEMBERSHIP_DAYS_LEFT = 999;

    public static boolean CHECK_MEMBERSHIP;

    public static boolean Muling;

    public static boolean DEATHWALKER;

    public static int DEATHS;

    public static int TOTAL_LOOT_BANK;

    public static boolean BUY_BOND;

    public static int COINS_IN_BANK;

    public static int MAIN_ITEM_AMOUNT;

    public static boolean RETRIVAL_EMPTY;

    public static boolean RESTOCKING;

    public static String MULE_NAME;

    public static int MULE_WORLD;

    public static int RESTOCK_AMOUNT;

    public static String RESTOCK_ITEM_NEEDED;

    public static String MAIN_ITEM_NAME;

    public static int COINS_NEEDED;

    public static int COINS_AMOUNT;


    public static List<Tile> safespotTiles = new ArrayList<>();


    // List of banned tiles (processed tiles)
    public static List<Tile> bannedTiles = new ArrayList<>();


    // Create a list to store the tiles with the blood rain object (ID: 51054)
    public static List<Tile> bloodRainTiles = new ArrayList<>();


    public static Tile closestJaguar;

    public static Tile jaguarTile;

    public static final List<Tile> jaguarTiles = List.of(
            new Tile(1398, 9631, 0), // Adjusted to reflect normal tile (example)
            new Tile(1398, 9634, 0),
            new Tile(1391, 9627, 0),
            new Tile(1387, 9631, 0),
            new Tile(1387, 9634, 0),
            new Tile(1391, 9638, 0),
            new Tile(1394, 9627, 0),
            new Tile(1394, 9638, 0)
    );

    public static final List<Tile> predefinedBloodRainTiles = List.of(
            new Tile(1389, 9629, 0),
            new Tile(1389, 9630, 0),
            new Tile(1389, 9631, 0),
            new Tile(1389, 9632, 0),
            new Tile(1389, 9633, 0),
            new Tile(1389, 9634, 0),
            new Tile(1389, 9635, 0),
            new Tile(1390, 9629, 0),
            new Tile(1390, 9635, 0),
            new Tile(1391, 9629, 0),
            new Tile(1391, 9635, 0),
            new Tile(1392, 9629, 0),
            new Tile(1392, 9635, 0),
            new Tile(1393, 9629, 0),
            new Tile(1393, 9635, 0),
            new Tile(1394, 9629, 0),
            new Tile(1394, 9635, 0),
            new Tile(1395, 9629, 0),
            new Tile(1395, 9630, 0),
            new Tile(1395, 9631, 0),
            new Tile(1395, 9632, 0),
            new Tile(1395, 9633, 0),
            new Tile(1395, 9634, 0),
            new Tile(1395, 9635, 0)
    );

    public static final List<Tile> stepUnderTiles = List.of(
            new Tile(1390, 9631, 0), // 1389 + 1 on X
            new Tile(1390, 9633, 0), // 1389 + 1 on X
            new Tile(1393, 9634, 0), // 9635 - 1 on Y
            new Tile(1394, 9633, 0), // 1395 - 1 on X
            new Tile(1394, 9631, 0), // 1395 - 1 on X
            new Tile(1391, 9630, 0)  // 9629 + 1 on Y
    );

    public static List<Tile> nextSafeSpotTiles = List.of(
            new Tile(1389, 9631, 0), // Area 1
            new Tile(1389, 9633, 0), // Area 2
            new Tile(1393, 9635, 0), // Area 3
            new Tile(1395, 9633, 0), // Area 4
            new Tile(1395, 9631, 0), // Area 5
            new Tile(1391, 9629, 0)  // Area 6
    );

    public static int tickCounter = 0;

    public static Tile closestBloodRainTile;

    public static int tickToDodge = 7;
    // Default time for the first dodge
    public static boolean dodgeAttack = false;// Trigger for dodge logic

    public static boolean stepUnderMessage = false;

    public static boolean LUNAR_CHEST_PENDING = false;

    public static boolean RESTOCK_SUPPLIES = false;

    public static boolean RESTORE_RUNENERGY = true;

    public static int KILLCOUNTER;

    public static boolean bloodAttackAnimation = false;

    public static boolean siphonPorjectileDetected = false;

    public static Tile porjectileStartTile;

    public static boolean useSpecialAttack = true;

    public static boolean BLOOD_MOON_DEAD = false;

    public static boolean safespotTilesChanged = true;

    public static boolean repairBarrowsArmour;

    public static int TOTAL_LOOT = 0;

    public static Tile walkingDestination;

    public static boolean walkToDestination;

}
