package MainPackage.DeathWalkerTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.rt4.Inventory;

public class DeathLumbridge extends Task {

    MainClass main;

    public DeathLumbridge(MainClass main) {
        super();
        name = "DeathLumbride";
    }

    @Override
    public boolean activate() {
        return Func.atLumbridge()
                && !GV.DEATHWALKER;
    }

    @Override
    public void execute() {

        System.out.println("DeathLumbridge active");
        GV.CURRENT_TASK="DeathLumbridge";

        if(!Func.wearingFullOutfit()){
            GV.DEATHWALKER=true;
        }
    }
}
