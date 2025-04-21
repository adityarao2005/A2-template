package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.List;

import org.json.JSONObject;

public record ActionResult(int cost, Extras extras, String status) {

    public static record Extras(List<String> biomes, List<String> creeks, List<String> sites) {
    }

    public static ActionResult fromJSON(String json) {
        var jsonObject = new JSONObject(json);
        var cost = jsonObject.getInt("cost");
        var status = jsonObject.getString("status");
        Extras _extras = null;
        if (jsonObject.has("extras") && !jsonObject.getJSONObject("extras").isEmpty()) {
            var extras = jsonObject.getJSONObject("extras");
            var biomes = extras.getJSONArray("biomes").toList().stream().map(Object::toString).toList();
            var creeks = extras.getJSONArray("creeks").toList().stream().map(Object::toString).toList();
            var sites = extras.getJSONArray("sites").toList().stream().map(Object::toString).toList();
            _extras = new Extras(biomes, creeks, sites);
        }
        return new ActionResult(cost, _extras, status);
    }
}
