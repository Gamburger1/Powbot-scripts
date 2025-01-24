package MainPackage.MulingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class MulingTeleport extends Task {

    public MulingTeleport(MainClass main) {
        super();
        name = "MulingTeleport";
    }

    @Override
    public boolean activate() {
        return Func.atGE()
                && !GV.Muling
                && !Bank.opened()
                && !GV.RESTOCKING
                && !GV.BUY_BOND;
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK= "MulingTeleport";
        System.out.println("MulingTeleport task activated");
        int currentWorld = Worlds.current().getNumber();
        final int[] p2p = {303, 304, 309, 310, 311, 312, 317, 327, 333, 334, 341, 343, 344, 350, 351, 352, 358, 359, 360, 367, 368, 375, 376, 395,
                463, 464, 465, 466, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522, 523, 524, 525};

        if(currentWorld == GV.MULE_WORLD){
            System.out.println("Switching worlds");
            int randomIndex = Random.nextInt(0, 47);
            int randomWorld = p2p[randomIndex];
            World world = new World(randomWorld, World.getNil().getPopulation(), World.getNil().textColor(), World.Type.MEMBERS, World.Server.RUNE_SCAPE, World.Specialty.NONE);
            System.out.println("random world is = " + randomWorld);
            if (Game.tab(Game.Tab.LOGOUT)) {
                if (world.hop()) {
                    System.out.println("hopped world successfully");
                }
            }
        } else{
           // Teleport back to botting area
        }
    }
}
