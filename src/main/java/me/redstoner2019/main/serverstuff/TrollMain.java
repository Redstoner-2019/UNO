package me.redstoner2019.main.serverstuff;

import me.redstoner2019.serverhandling.Client;
import me.redstoner2019.serverhandling.LocalNetworkScanner;
import me.redstoner2019.serverhandling.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrollMain extends Client {
    public static void main(String[] args) throws IOException {
        JSONObject main = new JSONObject(Util.readFile(new File("test/test.json")));
        System.out.println(main);
        JSONObject game = new JSONObject(main.getJSONObject("uno").toString());
        System.out.println(game);
        JSONObject version = new JSONObject();
        System.out.println(version);
        JSONArray versions = new JSONArray();
        System.out.println(versions);
        JSONObject type = new JSONObject();
        System.out.println(type);

        type.put("title","Update 1.0");
        type.put("changes","none");
        type.put("size",123456);
        type.put("file","path/to/file.jar");
        version.put("server",type);
        game.put("1.1",version);
        versions.put("1.1");
        game.put("versions",versions);
        main.put("uno",game);

        new File("test/").mkdirs();
        new File("test/test.json").createNewFile();

        Util.writeStringToFile(Util.prettyJSON(main.toString()),new File("test/test.json"));
    }
}
