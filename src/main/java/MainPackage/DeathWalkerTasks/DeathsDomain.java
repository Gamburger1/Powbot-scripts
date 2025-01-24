package MainPackage.DeathWalkerTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class DeathsDomain extends Task {


    public DeathsDomain(MainClass main) {
        super();
        name = "DeathsDomain";
    }

    @Override
    public boolean activate() {
        return Npcs.stream().name("Death").nearest().first().inViewport()
                && GV.DEATHWALKER;
    }

    @Override
    public void execute() {

        System.out.println("DeathsDomian active");
        GV.CURRENT_TASK="DeathsDomain";
        Component itemRetrival = Widgets.component(669,10,0);
        String[] wearableItems = { ""};

        if(Chat.chatting()){
            if(Chat.completeChat("Can I collect the items from that gravestone now?", "Bring my items here now; I'll pay your fee.")){
                Condition.wait(() -> itemRetrival.visible(), Random.nextInt(350, 450), 4);
            }
            else if (Chat.completeChat("Yes, have you got anything for me?")){
                Condition.wait(() -> itemRetrival.visible(), Random.nextInt(350, 450), 4);
            }
        }

        if (!Chat.chatting() && !itemRetrival.visible() && (!Func.wearingFullOutfit() || !Func.deathAllItemsRetrieved())){
            Npcs.stream().name("Death").nearest().first().interact("Talk-to");
            Condition.wait(() -> Chat.canContinue(), Random.nextInt(350, 450), 4);
        }

        if(itemRetrival.visible()) {
            final int itemAmount = Func.deathsItemRetrivalAmount();
            if(itemAmount > 0) {
                for (int i = 0; i < itemAmount; i++) {
                    System.out.println("i = " + i + " ,itemAmount = " + itemAmount);
                    int inventCount = Inventory.occupiedSlotCount();
                    String itemName = Widgets.component(669, 3, i).name();
                    if (itemName.contains("Rogue") || itemName.contains("Vyre") || itemName.contains("Rune pouch") || itemName.contains("gem bag")
                            || itemName.contains("Cosmic") || itemName.contains("shield") || itemName.contains("Defender") || itemName.contains("wealth")
                            || itemName.contains("staff") || itemName.contains("Drakan's")) {
                        if (Widgets.component(669, 3, i).interact("Select")) {
                            Condition.wait(() -> Inventory.occupiedSlotCount() != inventCount, Random.nextInt(350, 450), 4);
                        } else{
                            return;
                        }
                    }
                    if(i==itemAmount-1){
                        if(Movement.step(Players.local().tile())){
                            Condition.wait(() -> !itemRetrival.visible(), Random.nextInt(350, 450), 4);
                            GV.RETRIVAL_EMPTY=true;
                        }
                    }
                }
            }

        }

        //NEEDS TO BE COSTUMIZED TO YOUR OWN SCIRPT!!!!!

        /*if (!itemRetrival.visible()) {
            for (String item : wearableItems) {
                if(item.contains("Lava battlestaff") || item.contains("Dragon defender") || item.contains("shield")){
                    Inventory.stream().name(item).first().interact("Wield");
                }
                if (Inventory.stream().name(item).first().interact("Wear")) {
                    Condition.wait(() -> Inventory.stream().name(item).isEmpty(), Random.nextInt(350, 450), 4);
                    System.out.println("Wore " + item);
                }
            }
        }

        if(Func.wearingFullOutfit() && !itemRetrival.visible() && GV.RETRIVAL_EMPTY){
            if (Inventory.stream().name("Drakan's medallion").first().interact("Darkmeyer")) {
                GV.RETRIVAL_EMPTY=false;
                GV.DEATHWALKER=false;
                Condition.wait(() -> Func.atDarkmeyer(), Random.nextInt(200, 250), 45);
            }
        }*/

    }
}
