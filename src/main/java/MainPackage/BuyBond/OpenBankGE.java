package MainPackage.BuyBond;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.api.rt4.Inventory;

public class OpenBankGE extends Task {

    MainClass main;
    public OpenBankGE(MainClass main){
        super();
        super.name = "OpenBankGE";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.BUY_BOND
                && Func.atGE()
                && !GrandExchange.opened()
                && !Bank.opened()
                && Inventory.stream().nameContains("Old school bond").isEmpty();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "OpenBankGE";
        System.out.println("OpenBankGE task active");

        int bondID = 13190;
        int bondPrice = GrandExchange.getItemPrice(bondID) +  GrandExchange.getItemPrice(bondID) * 15 / 100;
        int bloodShardID = 24778;

        System.out.println("bond price: " + bondPrice);

        if (Inventory.stream().name("Coins").count(true) < bondPrice && Inventory.stream().id(bloodShardID).isEmpty()) {
            System.out.println("Opening bank");
            Bank.open();
            Condition.wait(() -> Bank.opened(), Random.nextInt(350, 450), 15);
        }
        if (Inventory.stream().name("Coins").count(true) >= bondPrice || Inventory.stream().id(bloodShardID).isNotEmpty()) {
            System.out.println("Opening GE");
            GrandExchange.open();
            Condition.wait(() -> GrandExchange.opened(), Random.nextInt(350, 450), 15);
        }


    }
}
