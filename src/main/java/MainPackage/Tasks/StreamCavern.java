package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.Objects;
import org.powbot.api.rt4.stream.item.InventoryItemStream;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamCavern extends Task {

    MainClass main;

    public StreamCavern(MainClass main) {
        super();
        super.name = "StreamCavern";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Func.atStreamCavern()
        && GV.RESTOCK_SUPPLIES;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "StreamCavern";
        System.out.println("StreamCavern task is active");
        int fishingAnimationID = 11042;
        Tile stoveTile = new Tile(1515, 9693, 0);
        Tile bridgeTile = new Tile(1520, 9689, 0);
        InventoryItemStream vialOfWater = Inventory.stream().name("Vial of water");
        InventoryItemStream moonlightGrub = Inventory.stream().name("Moonlight grub");
        InventoryItemStream moonlightGrubPaste = Inventory.stream().name("Moonlight grub paste");
        InventoryItemStream moonlightPotion = Inventory.stream().nameContains("Moonlight potion");
        InventoryItemStream moonlightPotionFour = Inventory.stream().name("Moonlight potion(4)");
        InventoryItemStream rawBream = Inventory.stream().name("Raw bream");
        InventoryItemStream pestleAndMortar = Inventory.stream().name("Pestle and mortar");
        InventoryItemStream cookedBream = Inventory.stream().name("Cooked bream");


        // Fishing supplies to drop
        String[] fishingSuppliesToDrop = {
                "Big fishing net", "Raw bream"
        };

        // Herblore supplies to drop
        String[] herbloreSuppliesToDrop = {
                "Pestle and mortar", "Vial of water", "Moonlight grub paste",
                "Moonlight grub", "Vial", "Moonlight potion(3)",
                "Moonlight potion(2)", "Moonlight potion(1)", "Rope",
                "Butterfly net", 
        };

        AtomicInteger currentDoses = new AtomicInteger();

        // Calculate current total doses of Moonlight potions
        Inventory.stream()
                .nameContains("Moonlight potion")
                .forEach(potion -> {
                    String dose = potion.name().replaceAll("[^0-9]", ""); // Extract numeric part
                    currentDoses.addAndGet(dose.isEmpty() ? 0 : Integer.parseInt(dose));
                });


        int totalPotionNeeded = 6; // Desired total number of Moonlight potion(4)
        int totalFoodNeeded = 20;  // Desired total number of Cooked bream


        // Calculate how many more potions and food items are needed
        int potionRestockAmount = (int) (totalPotionNeeded - moonlightPotionFour.count());
        int foodRestockAmount = (int) (totalFoodNeeded-Inventory.stream().name("Cooked bream").count());

        // Calculate total doses needed
        int totalStaticDosesNeeded = totalPotionNeeded * 4; // 24 doses for 6 potions
        int restockDosesNeeded = Math.max(0, totalStaticDosesNeeded - currentDoses.get());

        // Extra potions to account for variability in doses (e.g., (2) or (3) doses)
        int extraPotions = Math.max(0, (int) Math.ceil((totalPotionNeeded * 4 - currentDoses.get()) / 8.0));

        // Calculate how many more grubs and vials are needed
        int remainingGrubs = (int) Math.max(0, potionRestockAmount + extraPotions - moonlightGrub.count());
        int remainingVials = (int) Math.max(0, potionRestockAmount + extraPotions - vialOfWater.count());


        System.out.println("foodRestockAmount: " + foodRestockAmount);
        System.out.println("potionRestockAmount: " + potionRestockAmount);
        System.out.println("totalStaticDosesNeeded: " + totalStaticDosesNeeded);
        System.out.println("restockDosesNeeded: " + restockDosesNeeded);
        System.out.println("currentDoses: " + currentDoses.get());
        System.out.println("extraPotions: " + extraPotions);
        System.out.println("vialOfWater.count(): " + vialOfWater.count());
        System.out.println("pestleAndMortar.count(): " + pestleAndMortar.count());
        System.out.println("moonlightGrub.count(): " + moonlightGrub.count());

        // Calculate needed inventory slots for potion restocking
        int neededSlots = Math.max(0,
                (remainingGrubs + remainingVials) // Grubs and vials still needed
                        + 2                               // Pestle and mortar
        );

        System.out.println("neededSlots: " + neededSlots);


        // Open inventory tab if not open
        if(Game.tab() != Game.Tab.INVENTORY){
            System.out.println("Opened Inventory tab.");
            Game.tab(Game.Tab.INVENTORY);
        }


        // Step 1: Take Vials of Water if needed
        if (restockDosesNeeded > 0 && vialOfWater.count() < potionRestockAmount + extraPotions) {
            GameObject herbloreCrate = Objects.stream().name("Supply Crates").type(GameObject.Type.INTERACTIVE).within(Areas.STREAM_CAVERN).nearest().first();
            if (stoveTile.distanceTo(Players.local()) > 8) {
                Movement.step(stoveTile);
                Condition.sleep(Random.nextInt(250,450));
            }
            else if(neededSlots > Inventory.emptySlotCount()){
                System.out.println("Not enough empty slots in inventory, dropping some fish...");
                int dropAmount = neededSlots - Inventory.emptySlotCount();

                for (int i = 0; i < dropAmount; i++) {
                    int initialSlotCount = Inventory.emptySlotCount();
                    Item bream = Inventory.stream().name("Cooked bream").first();

                    if (bream != null && bream.interact("Drop")) {
                        System.out.println("Dropped Cooked bream. Waiting for inventory to update...");
                        Condition.wait(() -> Inventory.emptySlotCount() > initialSlotCount, 150, 10);
                    } else {
                        System.out.println("Failed to drop Cooked bream.");
                        break; // Stop the loop if drop fails
                    }
                }
            }
            else if(vialOfWater.count() < potionRestockAmount + extraPotions) {
                System.out.println("taking more supplies");
                long waterCount = Inventory.stream().name("Vial of water").count();
                if (herbloreCrate.valid() && herbloreCrate.interact("Take-from")) {
                    Condition.wait(() -> Widgets.component(219, 1, 3).visible(), 250, 25);
                    Chat.continueChat("Take herblore supplies.");
                    Condition.wait(() -> Inventory.stream().name("Vial of water").count() > waterCount, 250, 25);
                }
            }
        }

        // Step 2: Collect Moonlight Grub if needed
        else if (restockDosesNeeded > 0 && moonlightGrub.count() < potionRestockAmount + extraPotions && moonlightGrubPaste.isEmpty()) {
            GameObject grubbySapling = Objects.stream().name("Grubby sapling").type(GameObject.Type.INTERACTIVE).within(Areas.STREAM_CAVERN).nearest().first();
            if (grubbySapling.valid() && grubbySapling.interact("Collect-from")) {
                Condition.wait(() -> Inventory.stream().name("Moonlight grub").count() >= vialOfWater.count(), 450, 50);
            }
        }

        // Step 3: Process Grub into Paste
        else if (restockDosesNeeded > 0 && moonlightGrub.isNotEmpty() && moonlightGrubPaste.count() < potionRestockAmount+extraPotions) {
            Item pestle = Inventory.stream().name("Pestle and mortar").first();
            if (moonlightGrub.first().interact("Use")) {
                Condition.wait(() -> Inventory.selectedItem().id() == moonlightGrub.first().id(), 150, 5);
                pestle.click();
                Condition.wait(() -> Inventory.stream().name("Moonlight grub").isEmpty(), 450, 50);
            }
        }

        // Step 4: Mix Paste into Vials
        else if (restockDosesNeeded > 0 && moonlightPotion.count() < totalPotionNeeded+extraPotions) {
            Item moonlightPaste = Inventory.stream().name("Moonlight grub paste").first();
            Item vial = Inventory.stream().name("Vial of water").first();
            if (moonlightPaste.interact("Use")) {
                Condition.wait(() -> Inventory.selectedItem().id() == moonlightPaste.id(), 150, 5);
                vial.click();
                Condition.wait(() -> vialOfWater.isEmpty() || moonlightGrubPaste.isEmpty(), 450, 50);
            }
        }

        // Check if we have fewer than the desired amount of Moonlight potion(4)
        if (restockDosesNeeded == 0 && moonlightPotionFour.count() < totalPotionNeeded) {

        // Step 1: Create a list to store Moonlight potions with specific doses
            List<Item> nonFullPotions = new ArrayList<>();

        // Step 2: Loop through inventory and find Moonlight potions with doses (1), (2), and (3)
            Inventory.stream()
                    .nameContains("Moonlight potion(1)", "Moonlight potion(2)", "Moonlight potion(3)")
                    .forEach(nonFullPotions::add);

        // Step 3: Sort the list based on the potion name (implicit sorting due to the order of (1), (2), (3))
            nonFullPotions.sort(Comparator.comparing(Item::name));

            // Step 4: Use the lowest index item on the highest index item
            if (nonFullPotions.size() >= 2) {
                Item lowestDosePotion = nonFullPotions.get(0);
                Item highestDosePotion = nonFullPotions.get(nonFullPotions.size() - 1);

                // Step 5: Combine the potions
                if (lowestDosePotion.interact("Use")) {
                    Condition.wait(() -> Inventory.selectedItem().id() == lowestDosePotion.id(), 150, 5);
                    highestDosePotion.click(); // Use on the highest dose potion
                    System.out.println("Combined " + lowestDosePotion.name() + " with " + highestDosePotion.name());
                    Condition.sleep(500); // Small delay to ensure the combination is complete
                }
            } else {
                System.out.println("Not enough potions to combine.");
            }

        }

        // Step 6: Drop leftover supplies
        if (moonlightPotionFour.count() >= totalPotionNeeded && Inventory.stream().anyMatch(item ->
                Arrays.asList(herbloreSuppliesToDrop).contains(item.name()))) {

            System.out.println("Dropping herblore supplies");
            Inventory.stream()
                    .filter(item -> Arrays.asList(herbloreSuppliesToDrop).contains(item.name()))
                    .forEach(item -> item.interact("Drop"));

        }

        if(moonlightPotionFour.count()>totalPotionNeeded){
            Inventory.stream().name("Moonlight potion(4)").first().interact("Drop");
            Condition.wait(() -> Inventory.stream().name("Moonlight potion(4)").count() <= totalPotionNeeded, 100, 20);
        }

        // Restock food if needed
        if (foodRestockAmount > 0 && potionRestockAmount<=0) {
            System.out.println("Need more food, getting fishing supplies.");
            // Get fishing supplies
            if (Inventory.stream().name("Big fishing net").isEmpty()) {
                System.out.println("Big fishing net not found in inventory. Attempting to retrieve fishing supplies.");
                GameObject fishingCrate = Objects.stream().name("Supply Crates").type(GameObject.Type.INTERACTIVE).within(Areas.STREAM_CAVERN).nearest().first();
                if (Players.local().tile().distanceTo(bridgeTile) > 8) {
                    System.out.println("Player is far from the bridge tile. Moving closer.");
                    Movement.step(bridgeTile);
                    Condition.sleep(Random.nextInt(250,450));
                } else {
                    if (fishingCrate.valid() && fishingCrate.interact("Take-from")) {
                        System.out.println("Interacting with fishing supply crate.");
                        Condition.wait(() -> Widgets.component(219, 1, 1).visible(), 250, 25);
                    }
                    if (Widgets.component(219, 1, 1).visible()) {
                        System.out.println("Fishing supplies widget visible. Continuing chat to retrieve supplies.");
                        Chat.continueChat("Take fishing supplies.");
                        Condition.wait(() -> Inventory.stream().name("Big fishing net").isNotEmpty(), 100, 25);
                    } else {
                        System.out.println("Fishing supplies widget not visible. Failed to retrieve fishing net.");
                    }
                }
            }

            // Fish raw bream
            else if (rawBream.count() < foodRestockAmount && Players.local().animation() == -1) {
                System.out.println("Fishing raw bream. Current count: " + rawBream.count());
                GameObject fishingSpot = Objects.stream().name("Fishing spot").type(GameObject.Type.INTERACTIVE).within(Areas.STREAM_CAVERN).nearest().first();
                if (fishingSpot.inViewport()) {
                    System.out.println("Fishing spot in viewport. Interacting with it.");
                    if (fishingSpot.valid() && fishingSpot.interact("Fish")) {
                        Condition.wait(() -> Players.local().animation() == -1 && Inventory.isFull(), 450, 50);
                    } else {
                        System.out.println("Failed to interact with fishing spot.");
                    }
                } else {
                    System.out.println("Fishing spot not in viewport. Moving closer.");
                    Movement.step(bridgeTile);
                    Condition.wait(() -> fishingSpot.inViewport(), 250, 10);
                }
            }
            // Cook raw bream
            else if (rawBream.isNotEmpty() && Inventory.isFull()) {
                System.out.println("Inventory is full. Cooking raw bream.");
                GameObject cookingStove = Objects.stream().name("Cooking stove").type(GameObject.Type.INTERACTIVE).within(Areas.STREAM_CAVERN).nearest().first();
                if (cookingStove.valid() && cookingStove.interact("Cook")) {
                    System.out.println("Interacting with cooking stove.");
                    Condition.wait(() -> Inventory.stream().name("Raw bream").isEmpty(), 300, 50);
                } else {
                    System.out.println("Failed to interact with cooking stove.");
                }
            }
        }

        if(moonlightPotionFour.count()>=totalPotionNeeded && foodRestockAmount <= 0){
            System.out.println("Dropping fishing supplies");
            // Drop leftover supplies after cooking bream
            Inventory.stream()
                    .filter(item -> Arrays.asList(fishingSuppliesToDrop).contains(item.name()))
                    .forEach(item -> item.interact("Drop"));
            GV.RESTOCK_SUPPLIES=false;
        }





    }
}
