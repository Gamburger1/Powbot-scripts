package MainPackage.Tasks;

import MainPackage.MainClass;
import MainPackage.Task;
import MainPackage.Utility.GV;
import com.sun.tools.javac.Main;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.api.rt4.Widgets;

public class CheckMembershipDays extends Task {

    MainClass main;

    public CheckMembershipDays(MainClass main){
        super();
        super.name = "CheckMembershipDays";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return (GV.CHECK_MEMBERSHIP || GV.MEMBERSHIP_DAYS_LEFT==999)
                && Game.loggedIn();
    }


    @Override
    public void execute() {

        GV.CURRENT_TASK="CheckMembershipDays";

        if(Bank.opened()){
            System.out.println("Closing bank");
            Bank.close();
        }
        else if(GrandExchange.opened()){
            System.out.println("Closing Grand exchange");
            GrandExchange.close();
        }

        if(Game.tab(Game.Tab.ACCOUNT_MANAGEMENT)){
            String text = Widgets.component(109, 25).text();
            String numericText = text.replaceAll("[^0-9]", ""); // Removes all non-numeric characters
            GV.MEMBERSHIP_DAYS_LEFT = Integer.parseInt(numericText);
            GV.CHECK_MEMBERSHIP = false;
        }

    }
}
