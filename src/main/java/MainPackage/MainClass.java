package MainPackage;

import MainPackage.HelperTasks.walkToDestination;
import MainPackage.RepairBarrowsTasks.InteractBob;
import MainPackage.RepairBarrowsTasks.withdrawItems;
import MainPackage.Tasks.*;
import MainPackage.Utility.Func;
import MainPackage.Utility.GV;
import org.powbot.api.*;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.api.script.paint.TrackSkillOption;
import org.powbot.mobile.SettingsManager;
import org.powbot.mobile.ToggleId;
import org.powbot.mobile.drawing.FrameListener;
import org.powbot.mobile.drawing.FrameManager;
import org.powbot.mobile.script.ScriptManager;
import java.util.ArrayList;
import java.util.concurrent.Callable;


@ScriptManifest(name = "Kebab Blood Moon", description = "",
        version = "0.0.1", category = ScriptCategory.Runecrafting)

@ScriptConfiguration(
        name = "Mule name",
        description = "Whats your mules name?",
        defaultValue = "",
        allowedValues = {},
        optionType = OptionType.STRING,
        enabled = false,
        visible = true
)

@ScriptConfiguration(
        name = "Mule World",
        description = "What world to mule on?",
        defaultValue = "",
        allowedValues = {},
        optionType = OptionType.INTEGER,
        enabled = false,
        visible = true
)


public class MainClass extends AbstractScript implements FrameListener {


    private ArrayList<Task> taskList = new ArrayList<Task>(); // Keeps track of ticks

    int safespotID = 13015;
    int safespotSpawnAnimation = 10981;
    int jaguarAttackAnimation = 10959;
    int bloodMoonAttackAnimation = 11004;
    int npcIdleAnimationn = -1;
    int siphonProjectileID = 2227;
    int bloodRainObjectID = 51046;




    @Override
    public void onStart() {

        collectProjectileDestinationChanges();
        collectNpcAnimationChanges();
        collectMessageEvents();
        collectTickEvents();
        collectPaintCheckboxChanges();
        FrameManager.addListener(this);

        Events.disable();

        GV.MULE_NAME = (String) getOption("Mule name");
        GV.MULE_WORLD = getOption("Mule World");

        System.out.println("onStart() is running");

        SettingsManager.set(ToggleId.DismissRandomEvents, false);
        SettingsManager.set(ToggleId.DeathHandler,false);
        SettingsManager.set(ToggleId.DismissTradeWidget, false);
        SettingsManager.set(ToggleId.Humanizer, false);
        SettingsManager.set(ToggleId.UnexpectedIdle, false);
        SettingsManager.set(ToggleId.DismissLevelUps, false);

        Func.getTotalSupplies();

        if (Camera.getZoom() > 4) {
            Func.zoomOut();
        }

        if (Camera.pitch() < 99) {
            Camera.turnTo(0, 99);
            Condition.wait(() -> Camera.pitch() == 99, Random.nextInt(350, 450), 4);
        }

        Paint paint = PaintBuilder.newBuilder()
                .x(40)
                .y(45)
                .trackSkill(Skill.Strength, TrackSkillOption.Exp, TrackSkillOption.Level)
                .addString("Current Task: ", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return GV.CURRENT_TASK;
                    }
                })
                .addString("Total loot: ", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        // Format total loot with commas and calculate per hour in millions
                        double lootPerHour = getPerHour(GV.TOTAL_LOOT, ScriptManager.INSTANCE.getRuntime(true)) / 1_000_000.0;
                        String formattedLootPerHour = String.format("%.1fm/hr", lootPerHour < 0.1 ? 0.0 : lootPerHour);
                        return String.format("%,d (%s)", GV.TOTAL_LOOT, formattedLootPerHour);
                    }
                })
                .addString("Kills per hour: ", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return String.valueOf(String.format("%,d/hr",getPerHour(GV.KILLCOUNTER, ScriptManager.INSTANCE.getRuntime(true))));
                    }
                })
                .build();
        addPaint(paint);

        taskList.add(new BloodRainPhase(this));
        taskList.add(new JaguarPhase(this));
        taskList.add(new BloodMoonPhase(this));
        taskList.add(new EarthCavernWalk(this));
        taskList.add(new BossChamber(this));
        taskList.add(new AncientPrison(this));
        taskList.add(new LunarChestRoom(this));
        taskList.add(new BloodMoonLobby(this));
        taskList.add(new StreamCavern(this));
        taskList.add(new StreamCavernWalk(this));
        taskList.add(new walkToDestination(this));
        taskList.add(new InteractBob(this));
        taskList.add(new withdrawItems(this));
        taskList.add(new CamTorum(this));

    }

    private long getPerHour(long in, long time) {
        return (int) ((in) * 3600000D / time);
    }


    @Override
    public void poll() {

        /*if(GV.MEMBERSHIP_DAYS_LEFT==1 && GV.TOTAL_LOOT_BANK > 15000000){
            GV.BUY_BOND = true;
        }*/

        System.out.println("Poll has started.");
        // Remove tiles from the bloodRainTiles list where the object no longer exists
        if (GV.CURRENT_TASK == null) {
            System.out.println("[ERROR] GV.CURRENT_TASK is null!");
        } else if (GV.CURRENT_TASK.contains("Bloodrainphase") || GV.CURRENT_TASK.contains("Jaguar phase") || GV.CURRENT_TASK.contains("Blood moon phase")) {
            GV.bloodRainTiles.removeIf(tile -> Objects.stream().id(bloodRainObjectID).at(tile).isEmpty());
            System.out.println("Removed invalid tiles from blood rain tiles.");

            if (!GV.safespotTiles.isEmpty() && Npcs.stream().id(safespotID).at(GV.safespotTiles.get(0)).isEmpty()) {
                GV.safespotTiles.clear();
                System.out.println("Cleared safespot tiles because NPC is not at the true tile.");
            }
        }



        for (Task t : taskList) {
            String taskName = t.name;
            System.out.println(ScriptManager.INSTANCE.getRuntime(true) + " Checking: " + taskName);

            if (t.activate()) {
                System.out.println(ScriptManager.INSTANCE.getRuntime(true) + " Activated: " + taskName);
                t.execute();

                if (ScriptManager.INSTANCE.isStopping()) {
                    FrameManager.removeListener(this);
                    System.out.println("Script is stopping.");
                    break;
                }
            }
        }

        System.out.println(ScriptManager.INSTANCE.getRuntime(true) + " Finished looping through task list.");
    }

    public void collectPaintCheckboxChanges() {
        EventFlows.collectPaintCheckboxChanges(event -> {
            if (event.getCheckboxId().equals("Muler")) {
                GV.Muling = true;
            }
        });
    }

    @Override
    public void onFrame() {
        /*if (GV.CURRENT_TASK == null) {
            System.out.println("[ERROR] GV.CURRENT_TASK is null!");
        } else if (GV.CURRENT_TASK.contains("Bloodrainphase") || GV.CURRENT_TASK.contains("Jaguar phase") || GV.CURRENT_TASK.contains("Blood moon phase")) {
            Rendering.setColor(Color.getBLACK());
            for (Tile tile : GV.bloodRainTiles) {
                Rendering.drawPolygon(tile.matrix().bounds());
            }

            Rendering.setColor(Color.getGREEN());
            for (Tile tile : GV.safespotTiles) {
                Rendering.drawPolygon(tile.matrix().bounds());
            }
        }*/
    }

    public void collectTickEvents() {
        // Collect tick events
        EventFlows.collectTicks(tickEvent -> {
            if (GV.CURRENT_TASK != null && GV.CURRENT_TASK.contains("Jaguar phase")) {

                GV.tickCounter++; // Increment tick count
                System.out.println("Jaguar Phase Active. Incremented tickCounter to: " + GV.tickCounter);

                if (GV.tickCounter >= GV.tickToDodge) {
                    GV.dodgeAttack = true;
                    System.out.println("Tick threshold hit. dodgeAttack set to true.");
                }

            }
        });
    }

    public void collectMessageEvents() {
        EventFlows.collectMessages(event -> {
            String msg = event.getMessage();

            // Debugging message received
            System.out.println("Message received: " + msg);

            if(msg.contains("Oh dear, you are dead!")){
                GV.DEATHWALKER=true;
                GV.DEATHS++;
            }
            if(msg.contains("Welcome to Old School RuneScape.")) {
                GV.CURRENT_TASK="NotLoggedIn";
                GV.CHECK_MEMBERSHIP=true;
            }
            if(msg.contains("You drink the tea and find yourself feeling energised!")) {
                GV.RESTORE_RUNENERGY = false;
            }
            if(msg.contains("The Blood Moon of Peril is sufficiently distracted.")) {
                GV.useSpecialAttack=true;
                GV.LUNAR_CHEST_PENDING = true;
                GV.KILLCOUNTER++;
            }
            if(msg.contains("There you go, happy doing business with you!")) {
                GV.walkToDestination = true;
                GV.walkingDestination = new Tile(1439,9552,1);
                GV.repairBarrowsArmour = false;
            }
        });
    }


    public void collectNpcAnimationChanges() {
        EventFlows.collectNpcAnimationChanges(event -> {

            Npc npc = event.getNpc();

            // Check if Blood Moon is not dead
            if (!GV.BLOOD_MOON_DEAD) {

                // Handle safespot NPC animations
                if (npc.getId() == safespotID) {
                    if (event.getAnimation() == safespotSpawnAnimation && event.getPrevAnimation() == npcIdleAnimationn) {
                        System.out.println("Safespot NPC animation spawned, marking tiles as changed.");
                        GV.safespotTilesChanged = true;
                    } else if (event.getAnimation() == npcIdleAnimationn && event.getPrevAnimation() == safespotSpawnAnimation) {
                        System.out.println("Safespot NPC animation disappeared, marking tiles as changed.");
                        GV.safespotTilesChanged = true;
                    }
                }

                // Handle Blood Moon's attack-related animations
                if (npc.getName().equals("Blood jaguar") && event.getAnimation() == jaguarAttackAnimation) {
                    GV.tickCounter = 0;
                    if (GV.tickToDodge == 7) {
                        GV.tickToDodge = 6;
                    }
                }

                if (!GV.bloodAttackAnimation && npc.getName().equals("Blood Moon") && event.getAnimation() == bloodMoonAttackAnimation && event.getPrevAnimation() == npcIdleAnimationn) {
                    System.out.println("Blood Moon attack animation is 11004");
                    GV.bloodAttackAnimation = true;
                }

                if (npc.getName().equals("Blood Moon") && event.getAnimation() == npcIdleAnimationn && event.getPrevAnimation() == bloodMoonAttackAnimation) {
                    System.out.println("Blood Moon attack animation is -1");
                    GV.bloodAttackAnimation = false;
                    GV.stepUnderMessage = false;
                    GV.siphonPorjectileDetected = false;
                }
            }

            // Handle Enraged Blood Moon death detection
            if (npc.getName().equals("Enraged Blood Moon") && event.getAnimation() == bloodMoonAttackAnimation && event.getPrevAnimation() == npcIdleAnimationn) {
                System.out.println("Blood moon is dead, GV.BLOOD_MOON_DEAD = true");
                GV.BLOOD_MOON_DEAD = true;
            }

        });
    }


    public void collectProjectileDestinationChanges() {
        // Collect projectile destination change events
        EventFlows.collectProjectileDestinationChanges(event -> {
            // Only process if Blood Moon is not dead
            if (!GV.BLOOD_MOON_DEAD) {
                // Access projectile details directly from the event
                int projectileId = event.getId();

                // Check for the specific projectile ID and target name
                if (!GV.siphonPorjectileDetected && projectileId == siphonProjectileID && event.target().getName().equals("Blood Moon")) {
                    Tile startTile = new Tile(event.getStartX(), event.getStartY(), event.getStartZ());

                    // Compare startTile with the player's current tile
                    if (startTile.equals(Players.local().tile())) {
                        System.out.println("Projectile detected, siphonPorjectileDetected = true.");
                        GV.siphonPorjectileDetected = true;
                        GV.porjectileStartTile = startTile;
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        new MainClass().startScript();
    }

}
