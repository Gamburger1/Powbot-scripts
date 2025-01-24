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

import java.util.concurrent.Callable;

public class BossChamber extends Task {

    MainClass main;

    public BossChamber(MainClass main) {
        super();
        super.name = "BossChamber";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Func.atBossChamber();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "BossChamber";
        System.out.println("BossChamber task is active");
        Tile walkingTile = new Tile(1423, 9615,1);
        GameObject entrance = Objects.stream().name("Entrance").nearest(walkingTile).first();

        Func.checkBarrowsEquipment();

        if (Prayer.prayersActive()) { // Check if quick prayer is active
            System.out.println("Deactivating Quick Prayer...");
            Component quickPrayer = Widgets.component(897, 15);
            if (quickPrayer.interact("Deactivate")) {
                Condition.wait(() -> !Prayer.prayersActive(), Random.nextInt(50, 100), 12);
                System.out.println("Quick Prayer deactivated.");
            } else {
                System.out.println("Failed to deactivate Quick Prayer.");
            }
        }

        if(!entrance.inViewport()){
            System.out.println("Entrance not in view, webwalking to entrance.");
            Movement.builder(walkingTile).setAutoRun(true).setWalkUntil(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return Objects.stream().name("Entrance").nearest(walkingTile).first().inViewport();
                }
            }).move();
        }
        else{
            System.out.println("Entrance in view, interacting with entrance.");
            if(entrance.valid() && entrance.interact("Pass-through")){
                Condition.wait(() -> !Func.atBossChamber(), 200, 25);
                Condition.sleep(500);
            }
        }

    }
}
