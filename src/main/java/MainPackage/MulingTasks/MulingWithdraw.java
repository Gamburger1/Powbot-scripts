package MainPackage.MulingTasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;

public class MulingWithdraw extends Task {

    public MulingWithdraw(MainClass main) {
        super();
        name = "MulingWithdraw";
    }

    @Override
    public boolean activate() {
        return Func.atGE()
                && GV.Muling
                && !Func.gotAllItemsForTrade()
                && Bank.opened();
    }

    @Override
    public void execute() {

        GV.CURRENT_TASK = "MulingWithdraw";
        System.out.println("MulingWithdraw task activated");


        Bank.depositAllExcept("");
        if (Bank.withdrawModeNoted(true)) {
            String[] itemsToWithdraw = {"", ""};
            for (String item : itemsToWithdraw) {
                if(Bank.stream().name(item).isNotEmpty()){
                    if (Bank.withdraw(item, Bank.Amount.ALL)) {
                        Condition.wait(() -> Bank.stream().name(item).isEmpty(), Random.nextInt(350, 450), 4);
                    }else{
                        return;
                    }
                }
            }
        }

        if (Bank.close()) {
            Condition.wait(() -> !Bank.opened(), Random.nextInt(350, 450), 4);
        }

    }
}
