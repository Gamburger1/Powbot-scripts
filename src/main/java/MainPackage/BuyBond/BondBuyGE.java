package MainPackage.BuyBond;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class BondBuyGE extends Task {

    MainClass main;
    public BondBuyGE(MainClass main){
        super();
        super.name = "BondBuyGE";
        this.main = main;
    }


    @Override
    public boolean activate() {
        return GV.BUY_BOND
                && GrandExchange.opened();
    }

    @Override
    public void execute() {

        System.out.println("BondBuyGE task active");
        GV.CURRENT_TASK = "BondBuyGE";
        int bondID = 13190;
        int bondPrice = GrandExchange.getItemPrice(bondID) +  GrandExchange.getItemPrice(bondID) * 15 / 100;
        String sellingItemName = "";

        if(Inventory.stream().name(sellingItemName).isNotEmpty()){
            GeSlot selectedSlot = GrandExchange.availableSlot();
            Item item = Inventory.stream().name(sellingItemName).first();
            GrandExchangeItem bloodShardGE = GrandExchangeItem.Companion.fromName(sellingItemName);  // get geItem from name
            GrandExchange.startOffer(selectedSlot, item.id(),item.name(), false);
            GrandExchange.setPrice(bloodShardGE.getLow() - 300000);
            if (Widgets.component(465, 25, 54).click()) {                                     // Click confirm
                Condition.wait(() -> selectedSlot.isFinished(), Random.nextInt(350, 450), 15);
                System.out.println("blood shard fionished selling");
            }
            GrandExchange.collectAllToInventory();
        }

        else if(Func.slotsAvailable() && Inventory.stream().name("Old school bond").isEmpty() && Inventory.stream().name("Coins").count(true) >= bondPrice){
            GrandExchangeItem geItem = GrandExchangeItem.Companion.fromName("Old school bond");
            GrandExchange.createOffer(geItem, 1, geItem.getHigh() + geItem.getHigh() * 13 / 100, true);
            Condition.wait(() -> Func.slotsFinishedBuying(), Random.nextInt(125,300), 50);
            if(Func.collectAllToInventory()){
                System.out.println("Collecting bond to inventory");
            }
        }

        if(Inventory.stream().nameContains("Old school bond").isNotEmpty()){
            Movement.step(Players.local().tile());
            Condition.wait(() -> !GrandExchange.opened(), Random.nextInt(125,300), 4);
            System.out.println("Closing grand exchange ");
        }

    }
}
