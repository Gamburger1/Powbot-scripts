package MainPackage.RepairBarrowsTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Widgets;
import org.powbot.mobile.script.ScriptManager;

public class withdrawItems extends Task {

    MainClass main;

    public withdrawItems(MainClass main) {
        super();
        super.name = "withdrawItems";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.repairBarrowsArmour
                && Bank.inViewport()
                && !Func.atCamTorum()
                && Inventory.stream().name("Calcified moth").isEmpty()
                && Inventory.stream().name("Coins").count(true)<300000;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK="withdrawItems";

        int coinsAmount = 300000;

        if(!Bank.opened()){
            if(Bank.open()){
                System.out.println("interacted with bank, waiting for it to open..");
                Condition.wait(() -> Bank.opened(), 200, 6);
            }
        }
        if(Bank.opened()){
            System.out.println("Bank already open, withdrawing items");
            if(Inventory.stream().name("Calcified moth").isEmpty()){
                Bank.withdraw("Calcified moth",1);
                Condition.wait(() -> Inventory.stream().name("Calcified moth").isNotEmpty(), 200, 6);
            }
            if(Inventory.stream().name("Coins").count(true) < coinsAmount){
                if(Bank.stream().name("Coins").count(true) >= coinsAmount){
                    Bank.withdraw("Coins",coinsAmount);
                    Condition.wait(() -> Inventory.stream().name("Coins").count() >= coinsAmount, 200, 6);
                } else{
                    System.out.println("Not enough money to repair barrows gear, stopping script");
                    ScriptManager.INSTANCE.stop();
                }

            }
        }

        if(Inventory.stream().name("Coins").count(true) >= coinsAmount && Inventory.stream().name("Calcified moth").isNotEmpty()){
            Tile bobTile = new Tile(3233,3203,0);
            GV.walkingDestination = bobTile;
            GV.walkToDestination = true;
        }



    }
}
