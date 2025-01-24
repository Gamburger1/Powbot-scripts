package MainPackage.RestockingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Widgets;

public class RestockingWalk extends Task {

    Tile grandExchangeTile = new Tile(3161, 3490, 0);

    MainClass main;
    public RestockingWalk(MainClass main){
        super();
        super.name = "RestockingWalk";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return (GV.RESTOCKING || GV.BUY_BOND)
                && !Func.atGE();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK="RestockingWalk";

        Tile grandExchangeTile = new Tile(3161, 3490, 0);

        Movement.walkTo(grandExchangeTile.derive(Random.nextInt(1,4), Random.nextInt(1,4)));




    }
}
