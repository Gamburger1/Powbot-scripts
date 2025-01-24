package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.item.InventoryItemStream;

public class BloodMoonLobby extends Task {

    MainClass main;

    public BloodMoonLobby(MainClass main) {
        super();
        super.name = "BloodMoonLobby";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Func.atBloodMoonLobby();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "BloodMoonLobby";
        System.out.println("BloodMoonLobby task is active");
        InventoryItemStream specWeapon = Inventory.stream().name("Dragon dagger(p++)");
        GV.BLOOD_MOON_DEAD=false;
        GV.safespotTilesChanged=true;


       if(!Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(specWeapon.first().name()) && GV.useSpecialAttack){
            System.out.println("Spec restored, equipping the spec weapon.");
            if(specWeapon.first().interact("Wield")){
                Condition.wait(() -> Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(specWeapon.first().name()), Random.nextInt(50, 100), 15);
            }
        }
        else if(!Func.specialAttackEnabled() && GV.useSpecialAttack && Equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains(specWeapon.first().name())){
            System.out.println("Special attack not active, enabling..");
            Component specialAttackButton = Widgets.component(897, 31);

            // Activate special attack
            if(specialAttackButton.interact("Use")){
                System.out.println("Interacting with special attack widget..");
                Condition.wait(() -> Func.specialAttackEnabled(), 50, 15);
            }

        }

        else if(!Movement.running()){
            Movement.running(true);
            Condition.wait(() -> Movement.running(), 100, 12);

        }

        else if(Players.local().health() <= Players.local().maxHealth()-16 && Game.tab(Game.Tab.INVENTORY)){
            int initialHealthPercent = Players.local().healthPercent();
            Inventory.stream().name("Cooked bream").first().interact("Eat");
           Condition.wait(() -> Players.local().healthPercent() != initialHealthPercent, 200, 4);
            System.out.println("Health is 16 less than maxhealth, eating bream.");
        }

        else if(Func.prayerPoints() <= 50 && Game.tab(Game.Tab.INVENTORY)){
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
        else if (!Prayer.prayersActive()) { // Check if quick prayer is active
            System.out.println("Activating Quick Prayer...");
            Component quickPrayer = Widgets.component(897, 15);
            if(quickPrayer.interact("Activate")){
                Condition.wait(() -> Prayer.prayersActive(), Random.nextInt(50, 100), 12);
            } else {
                System.out.println("Failed to Activate Quick Prayer.");
            }
        }

        else{
            GameObject statue = Objects.stream().name("Statue").within(Areas.BLOODMOON_LOBBY).nearest().first();
            if(statue.valid() && statue.interact("Use")){
                Condition.wait(() -> Func.atBloodMoonArea(), 450, 8);
                Condition.sleep(600);
            }

        }

    }
}
