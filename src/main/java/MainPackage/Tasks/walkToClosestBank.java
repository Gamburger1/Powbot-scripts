package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.GV;

public class walkToClosestBank extends Task {

    MainClass main;

    public walkToClosestBank(MainClass main) {
        super();
        super.name = "walkToClosestBank";
        this.main = main;
    }


    @Override
    public boolean activate() {
        return GV.repairBarrowsArmour;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "walkToClosestBank";
        System.out.println("walkToClosestBank task is active");

    }
}
