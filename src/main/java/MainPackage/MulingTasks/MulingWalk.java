package MainPackage.MulingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Movement;

public class MulingWalk extends Task {

    public MulingWalk(MainClass main) {
        super();
        name = "MulingWalk";
    }

    @Override
    public boolean activate() {
        return GV.Muling
                && !Func.atGE();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK= "MulingWalk";
        System.out.println("MulingWalk task activated");

        Tile grandExchangeTile = new Tile(3161, 3490, 0);

        Movement.walkTo(grandExchangeTile);


    }
}
