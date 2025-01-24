package MainPackage.MulingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Npc;
import org.powbot.api.rt4.Npcs;

public class MulingClickBank extends Task {

    public MulingClickBank(MainClass main) {
        super();
        name = "MulingClickBank";
    }

    @Override
    public boolean activate() {
        return Func.atGE()
                && GV.Muling
                && !Bank.opened()
                && !Func.gotAllItemsForTrade();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "MulingClickBank";
        System.out.println("MulingClickBank task activated");

        System.out.println("MulingClickBank task active");
        Npc banker = Npcs.stream().name("Banker").nearest().first();
        Tile grandExchangeTile = new Tile(3161, 3490, 0);

        if (!Bank.opened()) {
            if (!banker.inViewport()) {
                System.out.println("Walking to bank area.");
                Movement.moveTo(grandExchangeTile);
            }
            if (banker.inViewport() && banker.valid()) {
                System.out.println("Opening bank");
                banker.interact("Bank");
                Condition.wait(() -> Bank.opened(), Random.nextInt(350, 450), 10);
            }
        }

    }
}
