package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

import java.util.Arrays;
import java.util.List;

public class LunarChestRoom extends Task {

    MainClass main;

    public LunarChestRoom(MainClass main) {
        super();
        super.name = "LunarChestRoom";
        this.main = main;
    }


    @Override
    public boolean activate() {
        return Func.atLunarChestArea();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "LunarChestRoom";
        System.out.println("LunarChestRoom task is active");
        Tile northEntranceTile = new Tile(1513, 9596,0);
        Tile southEntranceTile = new Tile(1513, 9567,0);
        GameObject lunarChest = Objects.stream().name("Lunar Chest").type(GameObject.Type.INTERACTIVE).within(Areas.CHEST_AREA).nearest().first();
        GV.RESTORE_RUNENERGY=true;

        Func.checkForRareItems();

        Func.dropUnwantedItems();

        if(GV.LUNAR_CHEST_PENDING){

            if (lunarChest.inViewport() && !Widgets.component(868,1,1).visible()){
                System.out.println("Lunar in view, interacting with chest");
                if(lunarChest.valid() && lunarChest.interact("Claim")){
                    if(Condition.wait(() -> Players.local().inMotion() || Players.local().distanceTo(lunarChest.tile())<=3, 100, 4)){
                        Condition.wait(() -> Widgets.component(868,1,1).text().equals("Lunar Chest")
                                && Widgets.component(868,1,1).visible(), 450, 10);
                    }
                }
            } else{
                System.out.println("Lunar chest not in view, stepping to chest");
                if(Movement.step(lunarChest.tile())){
                    if(Condition.wait(() -> Players.local().inMotion(), 100, 4)){
                        Condition.wait(() -> lunarChest.inViewport(), 450, 8);
                    }
                }

            }

            Component lunarChestWidget = Widgets.component(868,1,1);
            Component ItemWidget = Widgets.component(868,5,0);

            if(lunarChestWidget.visible()){
                System.out.println("Lunar chest widget visible.");
                if(!ItemWidget.name().isEmpty()){
                    int itemAmount = ItemWidget.itemStackSize();
                    int itemID = ItemWidget.itemId();
                    int itemTotalPrice = GrandExchange.getItemPrice(itemID) * itemAmount;
                    System.out.println("Lunar chest contains items, total item price: " + itemTotalPrice +  ", Item amount =  " + itemAmount);
                    if(Widgets.component(868,20).interact("Bank-all")){
                        GV.TOTAL_LOOT += itemTotalPrice;
                        GV.LUNAR_CHEST_PENDING=false;
                        Condition.wait(() -> Widgets.component(868,5,0).name().isEmpty(), 100, 6);
                    }
                }else{
                    if(Widgets.component(868,1,11).interact("Close")){
                        System.out.println("Lunar chest does not contains items, closing widget.");
                        Condition.wait(() -> !Widgets.component(868,1,1).visible(), 100, 6);
                    }
                }
            }
        }
        else{
            if(GV.RESTOCK_SUPPLIES){
                if(northEntranceTile.distanceTo(Players.local()) > 8){
                    System.out.println("North entrance not in view, .");
                    Movement.step(new Tile(Players.local().tile().getX(),Players.local().tile().getY()+Random.nextInt(8,13),Players.local().tile().getFloor()));
                    Condition.sleep(Random.nextInt(250,450));
                } else{
                    System.out.println("North entrance in view, interacting.");
                    GameObject northEntrance = Objects.stream().name("Entrance").nearest().first();
                    if(northEntrance.valid() && northEntrance.interact("Pass-through")){
                        Condition.wait(() -> Func.atStreamCavern(), 450, 12);
                        Condition.sleep(500);
                    } else{
                        Movement.step(northEntranceTile);
                    }
                }
            }
            else{
                if(southEntranceTile.distanceTo(Players.local()) > 8){
                    System.out.println("South entrance not in view, .");
                    Movement.step(new Tile(Players.local().tile().getX(),Players.local().tile().getY()-Random.nextInt(8,13),Players.local().tile().getFloor()));
                    Condition.sleep(Random.nextInt(250,450));
                } else{
                    System.out.println("South entrance in view, interacting.");
                    GameObject southEntrance = Objects.stream().name("Entrance").nearest().first();
                    if(southEntrance.valid() && southEntrance.interact("Pass-through")){
                        Condition.wait(() -> !Func.atLunarChestArea(), 450, 12);
                        Condition.sleep(500);
                    } else{
                        Movement.step(southEntranceTile);
                    }

                }
            }
        }

    }
}
