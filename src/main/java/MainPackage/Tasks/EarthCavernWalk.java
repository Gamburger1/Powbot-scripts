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
public class EarthCavernWalk extends Task {

    MainClass main;

    public EarthCavernWalk(MainClass main) {
        super();
        super.name = "EarthCavernWalk";
        this.main = main;
    }

    @Override
    public boolean activate() {

        return Func.atEarthCavern()
                && !GV.repairBarrowsArmour;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "EarthCavernWalk";
        System.out.println("EarthCavernWalk task is active");

        Tile walkingTile = new Tile(1390, 9676, 0);
        GameObject cookingStove = Objects.stream().name("Cooking stove").type(GameObject.Type.INTERACTIVE).within(Areas.EARTH_CAVERN).nearest().first();
        GameObject entrance = Objects.stream().name("Entrance").nearest(walkingTile).first();


        GV.siphonPorjectileDetected = false;
        GV.stepUnderMessage = false;
        GV.bloodAttackAnimation = false;

        if(!GV.safespotTiles.isEmpty()) {
            GV.safespotTiles.clear();
        }

        Func.getTotalSupplies();

        Func.checkBarrowsEquipment();

        if(GV.repairBarrowsArmour){
            System.out.println("Need to repair barrows armour, exit task..");
            return;
        }

        if (Prayer.prayersActive()) { // Check if quick prayer is active
            System.out.println("Deactivating Quick Prayer...");
            Component quickPrayer = Widgets.component(897, 15);
            if (quickPrayer.interact("Deactivate")) {
                Condition.wait(() -> !Prayer.prayersActive(), Random.nextInt(50, 100), 12);
                System.out.println("Quick Prayer deactivated.");
            } else {
                System.out.println("Failed to deactivate Quick Prayer.");
            }
        }

        if (Movement.energyLevel() < 70 && cookingStove.tile().distanceTo(Players.local().tile()) < 3) {
            System.out.println("Energy level low, interacting with cooking stove.");
            if (cookingStove.valid() && cookingStove.interact("Make-cuppa")) {
                Condition.wait(() -> Movement.energyLevel() == 100, 200, 10);
                System.out.println("Energy restored to 100%.");
            }
        }

        if (Players.local().health() <= Players.local().maxHealth() - 16 && Game.tab(Game.Tab.INVENTORY)) {
            System.out.println("Health is 16 less than max health, eating bream.");
            if (Inventory.stream().name("Cooked bream").first().interact("Eat")) {
                System.out.println("Cooked bream eaten.");
            } else {
                System.out.println("Failed to eat bream.");
            }
        }

        if (Func.prayerPoints() <= 50 && Game.tab(Game.Tab.INVENTORY) && GV.RESTOCK_SUPPLIES) {
            System.out.println("Prayer points low, drinking Moonlight potion.");
            String moonlightPotion = "";
            int oldPrayerPoints = Func.prayerPoints();

            if (Inventory.stream().name("Moonlight potion(1)").count() > 0) {
                moonlightPotion = "Moonlight potion(1)";
            } else if (Inventory.stream().name("Moonlight potion(2)").count() > 0) {
                moonlightPotion = "Moonlight potion(2)";
            } else if (Inventory.stream().name("Moonlight potion(3)").count() > 0) {
                moonlightPotion = "Moonlight potion(3)";
            } else if (Inventory.stream().name("Moonlight potion(4)").count() > 0) {
                moonlightPotion = "Moonlight potion(4)";
            }

            if (!moonlightPotion.isEmpty()) {
                System.out.println("Found Moonlight potion: " + moonlightPotion);
                if (Inventory.stream().name(moonlightPotion).first().interact("Drink")) {
                    Condition.wait(() -> Func.prayerPoints() != oldPrayerPoints, 200, 4);
                    System.out.println("Prayer points restored.");
                } else {
                    System.out.println("Failed to drink Moonlight potion.");
                }
            } else {
                System.out.println("No Moonlight potion found.");
            }
        }

        if (walkingTile.distanceTo(Players.local())>12) {
            System.out.println("Entrance not in view, stepping to entrance.");
            Movement.builder(walkingTile).setAutoRun(true).setWalkUntil(() -> Objects.stream().name("Entrance").nearest(walkingTile).first().inViewport()).move();
            System.out.println("Reached entrance.");
        } else if (walkingTile.distanceTo(Players.local())<=12) {
            System.out.println("Entrance in view, interacting with entrance.");
            if (entrance.interact("Pass-through")) {
                Condition.wait(() -> Func.atStreamCavern(), 450, 15);
                Condition.sleep(500);
                System.out.println("Successfully passed through entrance.");
            } else {
                System.out.println("Failed to interact with entrance.");
            }
        }
    }

}
