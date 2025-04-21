package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.teamXXX.Action.Direction;
import ca.mcmaster.se2aa4.island.teamXXX.Action.Parameters;

public abstract class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private BlockingQueue<Action> actions = new LinkedBlockingQueue<>();
    private BlockingQueue<ActionResult> results = new LinkedBlockingQueue<>();

    public class Drone {
        private Direction direction;
        private int batteryLevel;

        private Drone(Direction direction, int batteryLevel) {
            this.direction = direction;
            this.batteryLevel = batteryLevel;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getBatteryLevel() {
            return batteryLevel;
        }

        public ActionResult fly() throws InterruptedException {
            Action action = new Action("fly", null);
            actions.add(action);

            return results.take();
        }

        public ActionResult heading(Direction direction) throws InterruptedException {

            Action action = new Action("heading", new Parameters(direction));
            actions.add(action);

            return results.take();
        }

        public ActionResult echo(Direction direction) throws InterruptedException {

            Action action = new Action("echo", new Parameters(direction));
            actions.add(action);

            return results.take();
        }

        public ActionResult scan() throws InterruptedException {
            Action action = new Action("scan", null);
            actions.add(action);

            return results.take();
        }

        public ActionResult stop() throws InterruptedException {
            Action action = new Action("stop", null);
            actions.add(action);

            return results.take();
        }

    }

    protected abstract String algorithm(Drone drone) throws InterruptedException;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Future<String> future;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);

        future = executor.submit(() -> {
            Drone drone = new Drone(Direction.valueOf(direction), batteryLevel);
            return algorithm(drone);
        });

        logger.info("** The algorithm is running");
    }

    @Override
    public String takeDecision() {
        try {
            Action action = actions.take();
            logger.info("** Taking decision:\n {}", action.toJSONString().toString());
            return action.toJSONString().toString();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error taking decision", e);
        } finally {

            executor.shutdown();
        }
    }

    @Override
    public void acknowledgeResults(String s) {
        logger.info(s);
        ActionResult result = ActionResult.fromJSON(s);
        logger.info("** Acknowledging results:\n {}", result.toString());
        results.add(result);
    }

    @Override
    public String deliverFinalReport() {
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in the algorithm: " + e.getMessage();
        } finally {
            executor.shutdown();
            logger.info("** The algorithm has finished");
        }
    }

}
