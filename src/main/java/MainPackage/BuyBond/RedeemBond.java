package MainPackage.BuyBond;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Widgets;

public class RedeemBond extends Task {

    MainClass main;
    public RedeemBond(MainClass main){
        super();
        super.name = "RedeemBond";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.BUY_BOND
                && !GrandExchange.opened()
                && !Bank.opened()
                && Inventory.stream().nameContains("Old school bond").isNotEmpty();
    }

    @Override
    public void execute() {

        System.out.println("RedeemBond task active");
        GV.CURRENT_TASK = "RedeemBond";

        //if bond window not visible, then click redeem on bond.
        if(!Widgets.component(861,12,12).visible()){
            System.out.println("clicking redeem on bond");
            Inventory.stream().nameContains("Old school bond").first().interact("Redeem");
            Condition.wait(() -> Widgets.component(861,12,12).visible(), Random.nextInt(125,300), 6);
        }

        // if bond window visible but not the accept window than click 14 days bond and redeem.
        if(Widgets.component(861,12,12).visible() && !Widgets.component(289,8,8).visible()){
            System.out.println("clicking 14 days memb and waiting for accept window");
            Widgets.component(861,12,10).click();
            Condition.wait(() -> Widgets.component(289,8,8).visible(), Random.nextInt(125,300), 6);
        }

        // click accept and wait 5-6 seconds
        if(Widgets.component(289,8,8).visible()){
            System.out.println("clicking accept and waiting 5-6 seconds");
            if(Widgets.component(289,8,8).click()){
                GV.BUY_BOND=false;
                GV.MEMBERSHIP_DAYS_LEFT+=14;
                Condition.sleep(Random.nextInt(5000,6000));
            }

        }
    }
}
