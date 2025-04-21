package ca.mcmaster.se2aa4.island.teamXXX;

public class ConcreteExplorer extends Explorer {

    @Override
    protected String algorithm(Drone drone) throws InterruptedException {

        drone.scan();
        for (int i = 0; i < 3; i++) {
            drone.fly();
            drone.scan();
        }

        drone.stop();

        return "no creek found";
    }

}
