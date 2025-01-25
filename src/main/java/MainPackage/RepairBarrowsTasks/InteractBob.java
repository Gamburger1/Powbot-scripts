package MainPackage.RepairBarrowsTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

public class InteractBob extends Task {

    MainClass main;

    public InteractBob(MainClass main) {
        super();
        super.name = "InteractBob";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.repairBarrowsArmour
                && Npcs.stream().name("Bob").nearest().first().inViewport();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK="InteractBob";
        System.out.println("Interact bob task is active.");

        Npc bob = Npcs.stream().name("Bob").nearest().first();

        if(Func.checkAndRemoveBarrowsGear() && Inventory.stream().nameContains("25").isNotEmpty() && Inventory.stream().name("Coins").count(true)>=300000){ //  checks and removes all barrrows gear where name contains 25
            if(!Chat.chatting() && bob.interact("Repair")){
                System.out.println("chat not open, interact with bob");
                Condition.wait(() -> Chat.chatting(), 200, 6);
            }
            else if(Chat.chatting()){
                System.out.println("chat open, repair all items");
                if(Input.send("2")){
                    if(Condition.wait(() -> Inventory.stream().nameContains("25").isEmpty(), 200, 6)){
                        GV.walkToDestination = true;
                        GV.walkingDestination = new Tile(1439,9552,1);
                        GV.repairBarrowsArmour = false;
                    }
                }
            }
        }


    }
}
