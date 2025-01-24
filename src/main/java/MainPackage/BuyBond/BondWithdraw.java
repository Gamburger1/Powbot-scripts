package MainPackage.BuyBond;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import com.sun.tools.javac.Main;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.api.rt4.Inventory;
import org.powbot.mobile.script.ScriptManager;

public class BondWithdraw extends Task {

    MainClass main;
    public BondWithdraw(MainClass main){
        super();
        super.name = "BondWIthdraw";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.BUY_BOND
                && Func.atGE()
                && Bank.opened();
    }

    @Override
    public void execute() {

        System.out.println("BondWIthdraw task active");
        GV.CURRENT_TASK = "BondWIthdraw";

        int bondID = 13190;
        int bondPrice = GrandExchange.getItemPrice(bondID) +  GrandExchange.getItemPrice(bondID) * 15 / 100;
        System.out.println("bond price is = " + bondPrice);
        GV.MAIN_ITEM_AMOUNT= (int) Bank.stream().name("Blood shard").count(true);
        GV.COINS_IN_BANK = (int) Bank.stream().name("Coins").count(true);

        Bank.depositAllExcept("");



        if(GV.COINS_IN_BANK<bondPrice){
            if(GV.MAIN_ITEM_AMOUNT>1){
                Bank.withdrawModeNoted(true);
                Bank.withdraw("Blood shard", 2);
                Condition.wait(() -> Inventory.stream().name("Blood shard").isNotEmpty(), Random.nextInt(300, 450), 4);
                System.out.println("withdrawing blood shards");
                Bank.withdraw("Coins", Bank.Amount.ALL);
                Condition.wait(() -> Inventory.stream().name("Coins").isNotEmpty(), Random.nextInt(300, 450), 4);
            } else{
                System.out.println("Not enough money for bond, stopping script.");
                ScriptManager.INSTANCE.stop();
            }
        }
        else{
            System.out.println("withdrawing coins");
            Bank.withdraw("Coins", Bank.Amount.ALL);
            Condition.wait(() -> Inventory.stream().name("Coins").count(true)>= bondPrice, Random.nextInt(300, 450), 4);
        }

        if(Inventory.stream().name("Coins").isNotEmpty() || Inventory.stream().name("Blood shard").isNotEmpty()){
            Bank.close();
            Condition.wait(() -> !Bank.opened(), Random.nextInt(300, 450), 4);
        }

    }
}
