package MainPackage.HelperTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Players;

public class walkToDestination extends Task {

    MainClass main;

    public walkToDestination(MainClass main) {
        super();
        super.name = "walkToDestination";
        this.main = main;
    }


    @Override
    public boolean activate() {
        return GV.repairBarrowsArmour
                && GV.walkToDestination;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "walkToDestination";
        System.out.println("walkToDestination task is active");

        if(GV.walkingDestination != null && Players.local().tile().distanceTo(GV.walkingDestination)>6){
            System.out.println("walking to tile " + GV.walkingDestination);
            Movement.builder(GV.walkingDestination).setAutoRun(true).setWalkUntil(() -> Players.local().tile().equals(GV.walkingDestination)).move();
        }

        if(Players.local().tile().distanceTo(GV.walkingDestination)<=6){
            System.out.println("Arrived at destination.");
            GV.walkToDestination = false;
            GV.walkingDestination = null;
        }

    }
}
