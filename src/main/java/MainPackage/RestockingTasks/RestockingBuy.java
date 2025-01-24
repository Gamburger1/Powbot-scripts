package MainPackage.RestockingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class RestockingBuy extends Task {

    MainClass main;

    public RestockingBuy(MainClass main) {
        super();
        super.name = "RestockingBuy";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return GV.RESTOCKING
                && Func.atGE()
                && GrandExchange.opened()
                && !Bank.opened();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK="RestockingBuy";

        System.out.println("Restick item amount = " + GV.RESTOCK_AMOUNT);

        if(Inventory.stream().name(GV.MAIN_ITEM_NAME).isNotEmpty()){
            GeSlot selectedSlot = GrandExchange.availableSlot();
            Item bloodShard = Inventory.stream().name(GV.MAIN_ITEM_NAME).first();
            GrandExchangeItem bloodShardGE = GrandExchangeItem.Companion.fromName(GV.MAIN_ITEM_NAME);  // get geItem from name
            GrandExchange.startOffer(selectedSlot, bloodShard.id(),bloodShard.name(), false);
            GrandExchange.setPrice(bloodShardGE.getLow() - 300000);
            if (Widgets.component(465, 25, 54).click()) {                                     // Click confirm
                Condition.wait(() -> selectedSlot.isFinished(), Random.nextInt(350, 450), 15);
                System.out.println( GV.MAIN_ITEM_NAME + " finished selling");
            }
            GrandExchange.collectAllToInventory();
        }
        else if(Func.slotsAvailable()){
            GrandExchangeItem geItem = GrandExchangeItem.Companion.fromName(GV.RESTOCK_ITEM_NEEDED);
            System.out.println("fromName ID: " + GrandExchangeItem.Companion.fromName(GV.RESTOCK_ITEM_NEEDED).getId());
            GrandExchange.createOffer(geItem, GV.RESTOCK_AMOUNT, geItem.getHigh() + geItem.getHigh() * 5 / 100, true);
            Condition.wait(() -> Func.slotsFinishedBuying(), Random.nextInt(125,300), 50);
            if(Func.collectAllToBank()){
                GV.RESTOCKING=false;
                System.out.println("GV.RESTOCKING = " + GV.RESTOCKING);
            }
        }

        if(!GV.RESTOCKING){
            Movement.step(Players.local().tile());
            Condition.wait(() -> !GrandExchange.opened(), Random.nextInt(125,300), 4);
            System.out.println("Closing grand exchange ");
        }



    }
}
