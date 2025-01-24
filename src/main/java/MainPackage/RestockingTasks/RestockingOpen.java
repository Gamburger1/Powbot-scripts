package MainPackage.RestockingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.api.rt4.Inventory;

public class RestockingOpen extends Task {

    MainClass main;

    public RestockingOpen(MainClass main){
        super();
        super.name = "RestockingOpen";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.RESTOCKING
                && Func.atGE()
                && !GrandExchange.opened()
                && !Bank.opened();
    }

    @Override
    public void execute() {

        System.out.println("RestockingOpen task active");
        GV.CURRENT_TASK = "RestockingOpen";

        if (Inventory.stream().name("Coins").isEmpty()) {
            System.out.println("Opening bank");
            Bank.open();
            Condition.wait(() -> Bank.opened(), Random.nextInt(350, 450), 15);
        }
        if (Inventory.stream().name("Coins").isNotEmpty()) {
            System.out.println("Opening GE");
            GrandExchange.open();
            Condition.wait(() -> GrandExchange.opened(), Random.nextInt(350, 450), 15);
        }


    }
}
