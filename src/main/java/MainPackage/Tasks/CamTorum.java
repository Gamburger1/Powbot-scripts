package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.item.InventoryItemStream;

public class CamTorum extends Task {

    MainClass main;

    public CamTorum(MainClass main) {
        super();
        super.name = "CamTorum";
        this.main = main;
    }


    @Override
    public boolean activate() {
        return Func.atCamTorum();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "CamTorum";
        System.out.println("CamTorum task is active");

        InventoryItemStream specWeapon = Inventory.stream().name("Dragon dagger(p++)");


        // Check if there is anything else in the inventory besides the specified weapon
        if (Func.checkAndWearItems() && Inventory.stream().filter(item -> !item.name().equals("Dragon dagger(p++)")).isNotEmpty()) {
            System.out.println("Found items other than the special weapon in inventory. Checking bank visibility.");

            // Check if the bank is in the viewport
            if (!Bank.inViewport()) {
                System.out.println("Bank is not in the viewport. Walking to the closest bank.");
                Movement.builder(Bank.nearest()).setAutoRun(true).setWalkUntil(() -> Players.local().tile().distanceTo(Bank.nearest())<=2).move();
                Condition.wait(() -> Bank.inViewport(), 200, 15);
            }

            // Open the bank if it's in the viewport
            if (Bank.inViewport()) {
                if (!Bank.opened()) {
                    if (Bank.open()) {
                        System.out.println("Interacted with bank, waiting for it to open...");
                        Condition.wait(() -> Bank.opened(), 200, 6);
                    }
                }

            }

            // If bank opened, deposit every except special attack weapon
            if (Bank.opened()) {
                System.out.println("Bank is open. Depositing items except for the special weapon.");

                // Deposit all items except the special weapon
                if (Bank.depositAllExcept("Dragon dagger(p++)")) {
                    Condition.wait(() -> Inventory.stream().filter(item -> !item.name().equals("Dragon dagger(p++)")).isEmpty(), 200, 6);
                    System.out.println("All other items deposited successfully.");
                } else {
                    System.out.println("Failed to deposit items.");
                }
            }
        } else {
            System.out.println("Inventory contains only the special weapon. Walk to and interact with entrance.");
        }

        // Example entrance tile (update with the correct coordinates)
        Tile entranceTile = new Tile(1439, 9593, 1);

        // If statement to check conditions and perform actions
        if (specWeapon.isNotEmpty() && Inventory.stream().filter(item -> !item.name().equals("Dragon dagger(p++)")).isEmpty()) {
            System.out.println("Only special weapon in inventory and all items equipped. Walking to entrance tile.");
            GameObject entrance = Objects.stream().name("Entrance").nearest(entranceTile).first();


            // Walk to the entrance tile if not already there
            if (!entrance.inViewport()) {
                Movement.walkTo(entranceTile);
                Condition.wait(() -> entrance.inViewport(), 200, 15);
            }

            // Interact with "Pass-through" if the entrance tile is in the viewport
            if (entrance.inViewport()) {
                if (entrance.valid() && entrance.interact("Pass-through")) {
                    Condition.wait(() -> Func.atBossChamber(), 450, 15);
                    Condition.sleep(500);
                    System.out.println("Interacted with the entrance.");
                } else {
                    System.out.println("Failed to interact with the entrance.");
                }
            }
        }

    }
}
