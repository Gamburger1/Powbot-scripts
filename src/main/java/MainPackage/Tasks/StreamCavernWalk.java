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

public class StreamCavernWalk extends Task {

    MainClass main;

    public StreamCavernWalk(MainClass main) {
        super();
        super.name = "StreamCavernWalk";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Func.atStreamCavern()
                && !GV.RESTOCK_SUPPLIES;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "StreamCavernWalk";
        System.out.println("StreamCavernWalk task is active");

        Tile walkingTile = new Tile(1526, 9671, 0);

        if(!GV.RESTOCK_SUPPLIES){
            if(walkingTile.distanceTo(Players.local())>6){
                System.out.println("Entrance not in view, webwalking to entrance.");
                if(Movement.step(walkingTile)){
                    Condition.sleep(Random.nextInt(250,450));
                }
            }
            else if(walkingTile.distanceTo(Players.local())<=6){
                GameObject entrance = Objects.stream().name("Entrance").nearest().first();
                System.out.println("Entrance in view, interacting with entrance.");
                if(entrance.valid() && entrance.interact("Pass-through")){
                    Condition.wait(() -> Func.atLunarChestArea(), 450, 15);
                    Condition.sleep(500);
                }
            }
        }

    }
}
