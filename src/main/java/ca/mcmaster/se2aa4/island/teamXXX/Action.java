package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public record Action(String action, Parameters parameters) {

    public static enum Direction {
        N, S, E, W
    }

    public static record Parameters(Direction direction) {

        public JSONObject toJSONString() {
            return new JSONObject()
                    .put("direction", direction.name());
        }
    }

    public JSONObject toJSONString() {
        JSONObject object = new JSONObject()
                .put("action", action);

        if (parameters != null) {
            object = object.put("parameters", parameters.toJSONString());
        }

        return object;
    }
}
