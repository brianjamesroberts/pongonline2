package unfairtools.com.pongonline2;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by brianroberts on 9/24/16.
 */

class InfoObject {

    public String toJSon(){
        return this.toJSon(this);
    }

    private String toJSon(InfoObject obj) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("action", obj.action);
            jo.put("intVals",obj.intVals);
            JSONArray jsonArr = new JSONArray();
            int i = 0;
            if(obj.vals!=null)
                for (String str : obj.vals ) {
                    jsonArr.put(i++,str);
                }
            jo.put("vals",jsonArr);
            jo.put("appName",obj.appName);

            i = 0;
            JSONArray jsonArr2 = new JSONArray();
            if(obj.intVals!=null)
                for(Integer ints : obj.intVals){
                    jsonArr2.put(i++,ints);
                }
            jo.put("intVals",jsonArr2);

            JSONArray jMap = new JSONArray();
            if(maps!=null) {
                for (String[] a : maps) {
                    JSONArray json3 = new JSONArray();
                    int z = 0;
                    for (String b : a) {
                        json3.put(z++, b);
                    }
                    jMap.put(json3);
                }
                jo.put("maps", jMap);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return jo.toString();
    }
    public String action;
    public String[] vals;
    public String appName;
    public int[] intVals;
    String[][] maps;

}