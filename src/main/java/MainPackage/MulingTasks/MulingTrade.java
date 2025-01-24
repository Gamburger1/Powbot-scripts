package MainPackage.MulingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class MulingTrade extends Task {

    public MulingTrade(MainClass main) {
        super();
        name = "MulingTrade";
    }

    @Override
    public boolean activate() {
        return Func.atGE()
                && GV.Muling
                && !Bank.opened()
                && (Func.gotAllItemsForTrade() || Trade.isOpen());
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK= "MulingTrade";
        System.out.println("MulingTrade task activated");
        int currentWorld = Worlds.current().getNumber();
        String playerName = GV.MULE_NAME;
        String[] itemsToWithdraw = {""};

        if(currentWorld != GV.MULE_WORLD){
            System.out.println("Switching worlds");
            final int p2p = GV.MULE_WORLD;
            World world = new World(p2p, World.getNil().getPopulation(), World.getNil().textColor(), World.Type.MEMBERS, World.Server.RUNE_SCAPE, World.Specialty.NONE);
            if (Game.tab(Game.Tab.LOGOUT)) {
                world.hop();
            }
        }
        else{
            if(Players.stream().name(playerName).nearest().first().inViewport() && !Trade.isOpen()){
                System.out.println("Player is in viewport = " + playerName);
                if(Players.stream().name(playerName).nearest().first().interact("Trade")){
                    Condition.wait(() -> Trade.isOpen(Trade.Screen.First), Random.nextInt(350, 450), 15);
                } else{
                    return;
                }
            }

            if (Trade.isOpen(Trade.Screen.First)) {
                System.out.println("First trade screen open");
                for (String item : itemsToWithdraw) {
                    if(Inventory.stream().name(item).isNotEmpty()){
                        if (Inventory.stream().name(item).first().interact("Offer-All")) {
                            Condition.wait(() -> Inventory.stream().name(item).isEmpty(), Random.nextInt(350, 450), 4);
                        }
                    }
                }

                if(Inventory.stream().name(itemsToWithdraw).isEmpty() && Trade.isOpen()){
                    Condition.sleep(Random.nextInt(3100,3300));
                    if(Trade.accept()){
                        Condition.wait(() -> Trade.hasAccepted(Trade.Party.Them), Random.nextInt(350, 450), 5);
                    } else {
                        return;
                    }
                }
            }

            if(Trade.isOpen(Trade.Screen.Second)){
                if(Trade.accept()){
                    Condition.wait(() -> Trade.hasAccepted(Trade.Party.Them), Random.nextInt(350, 450), 5);
                }
                if(Inventory.stream().name(itemsToWithdraw).isEmpty()){
                    GV.Muling = false;
                }
            }

        }

    }
}
