package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Areas;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Objects;
import org.powbot.api.rt4.Players;

import java.util.concurrent.Callable;

public class AncientPrison extends Task {

    MainClass main;

    public AncientPrison(MainClass main) {
        super();
        super.name = "AncientPrison";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Func.atAncientPrison();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "AncientPrison";
        System.out.println("AncientPrison task is active");
        Tile chestWalkingTile = new Tile(1355, 9538,0);
        Tile bossWalkingTile = new Tile(1386, 9591,0);
        Tile stoveWalkingTile = new Tile(1352,9581,0);

        Func.checkBarrowsEquipment();

        Func.getTotalSupplies();

        if(!Movement.running()){
            Movement.running(true);
            Condition.wait(() -> Movement.running(), 100, 12);

        }

        if(GV.RESTORE_RUNENERGY){
            if(stoveWalkingTile.distanceTo(Players.local())>8){
                System.out.println("Cooking stove not in view, webwalking to stove.");
                Movement.builder(stoveWalkingTile).setAutoRun(true).setWalkUntil(() -> stoveWalkingTile.distanceTo(Players.local())<=8).move();
            }
            if(stoveWalkingTile.matrix().inViewport()){
                System.out.println("Stove in view, interacting with entrance.");
                GameObject cookingStove = Objects.stream().name("Cooking stove").type(GameObject.Type.INTERACTIVE).within(Areas.ANCIENT_PRISON).nearest().first();
                if(cookingStove.valid() && cookingStove.interact("Make-cuppa")){
                    Condition.wait(() -> !GV.RESTORE_RUNENERGY || Players.local().interacting().valid(), 450, 20);
                }
            }
        }
        else if(!GV.RESTOCK_SUPPLIES && !GV.LUNAR_CHEST_PENDING && !GV.RESTORE_RUNENERGY){
            if(bossWalkingTile.distanceTo(Players.local())>8){
                System.out.println("supplies not needed, webwalking to boss entrance.");
                Movement.builder(bossWalkingTile).setAutoRun(true).setWalkUntil(() -> bossWalkingTile.distanceTo(Players.local())<=8).move();
            }
            if(bossWalkingTile.distanceTo(Players.local())<=8){
                System.out.println("Boss entrance in view, interacting with entrance.");
                GameObject bossEntrance = Objects.stream().name("Entrance").nearest().first();
                if(bossEntrance.valid() && bossEntrance.interact("Pass-through")){
                    Condition.wait(() -> Func.atBloodMoonLobby() || Players.local().interacting().valid(), 450, 25);
                    Condition.sleep(500);
                }
            }

        }
        else if(GV.RESTOCK_SUPPLIES || GV.LUNAR_CHEST_PENDING && !GV.RESTORE_RUNENERGY){
            if(chestWalkingTile.distanceTo(Players.local())>8){
                System.out.println("supplies needed or lunar chest pending, webwalking to chest entrance.");
                Movement.builder(chestWalkingTile).setAutoRun(true).setWalkUntil(() -> chestWalkingTile.distanceTo(Players.local())<=8).move();
            }
            if(chestWalkingTile.distanceTo(Players.local())<=8) {
                System.out.println("Chest entrance in view, interacting with entrance.");
                GameObject chestEntrance = Objects.stream().name("Entrance").nearest().first();
                if (chestEntrance.valid() && chestEntrance.interact("Pass-through")) {
                    Condition.wait(() -> Func.atLunarChestArea() || Players.local().interacting().valid(), 450, 15);
                    Condition.sleep(500);
                }
            }
        }



    }
}
