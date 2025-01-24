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

public class RestockingWithdraw extends Task {

    public RestockingWithdraw(MainClass main) {
        super();
        name = "RestockingWithdraw";
    }

    @Override
    public boolean activate() {
        return GV.RESTOCKING
                && Func.atGE()
                && Bank.opened();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK="RestockingWithdraw";

        switch(GV.RESTOCK_ITEM_NEEDED){
            case "Ancient brew(4)":
                int brewPrice = GrandExchange.getItemPrice(26340) + GrandExchange.getItemPrice(26340) * 15 / 100;
                GV.RESTOCK_AMOUNT = Math.round((float) GV.COINS_IN_BANK / brewPrice);
                if(GV.RESTOCK_AMOUNT > 1000){
                    GV.RESTOCK_AMOUNT = 1000;
                }
                GV.COINS_NEEDED = brewPrice * GV.RESTOCK_AMOUNT;
                break;
            case "Jug of wine":
                GV.RESTOCK_AMOUNT = 6000;
                GV.COINS_NEEDED += GrandExchange.getItemPrice(1993) * GV.RESTOCK_AMOUNT + 100000;
                break;
            case "Dodgy necklace":
                int dodgyPrice = GrandExchange.getItemPrice(21143) + GrandExchange.getItemPrice(21143) * 15 / 100;
                GV.RESTOCK_AMOUNT = Math.round((float) GV.COINS_IN_BANK / dodgyPrice);
                System.out.println("Dodgy price: " + dodgyPrice);
                if(GV.RESTOCK_AMOUNT > 1000){
                    GV.RESTOCK_AMOUNT=1000;
                }
                GV.COINS_NEEDED = dodgyPrice * GV.RESTOCK_AMOUNT;
                break;
            case "Cosmic rune":
                int cosmicPrice = GrandExchange.getItemPrice(564) + GrandExchange.getItemPrice(564) * 15 / 100;
                GV.RESTOCK_AMOUNT = Math.round((float) GV.COINS_IN_BANK / cosmicPrice);
                if(GV.RESTOCK_AMOUNT > 18000){
                    GV.RESTOCK_AMOUNT=18000;
                }
                GV.COINS_NEEDED = cosmicPrice * GV.RESTOCK_AMOUNT;
                break;
            case "Ring of wealth (5)":
                GV.RESTOCK_AMOUNT = Random.nextInt(5,10);
                GV.COINS_NEEDED += GrandExchange.getItemPrice(11980) * GV.RESTOCK_AMOUNT + 100000;
                break;
            case "Lobster":
                GV.RESTOCK_AMOUNT = Random.nextInt(200,500);
                GV.COINS_NEEDED += GrandExchange.getItemPrice(379) * GV.RESTOCK_AMOUNT + GrandExchange.getItemPrice(379) * 15 / 100;
                break;
            case "Monkfish":
                GV.RESTOCK_AMOUNT = Random.nextInt(100,300);
                GV.COINS_NEEDED += GrandExchange.getItemPrice(7946) * GV.RESTOCK_AMOUNT + GrandExchange.getItemPrice(7946) * 15 / 100;
                break;
            case "Salmon":
                GV.RESTOCK_AMOUNT = Random.nextInt(200,500);
                GV.COINS_NEEDED += GrandExchange.getItemPrice(329) * GV.RESTOCK_AMOUNT + GrandExchange.getItemPrice(329) * 15 / 100;
                break;
        }

        GV.COINS_AMOUNT = Integer.parseInt(String.valueOf(Bank.stream().name("Coins").count(true)));
        System.out.println("Coins needed = " + GV.COINS_NEEDED);

        if(GV.COINS_AMOUNT<GV.COINS_NEEDED){
            if(GV.MAIN_ITEM_AMOUNT>0){
                Bank.withdraw("Blood shard", 1);
                System.out.println("withdrawing blood shards");
            }
        }

        if(Bank.stream().name("Coins").isNotEmpty()) {
            if (Bank.withdraw("Coins", Bank.Amount.ALL)) {
                System.out.println("Withdraw coins");
            }
        }

        //TELEPORT BACK TO BOTTING AREA

        if(Bank.opened()){
            Bank.close();
            Condition.wait(() -> !Bank.opened(), Random.nextInt(125,300), 25);
        }




    }
}
