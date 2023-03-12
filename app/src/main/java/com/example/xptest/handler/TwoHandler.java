package com.example.xptest.handler;

import com.example.xptest.Store;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

public class TwoHandler implements ActionHandler {
    @Override
    public String action() {
        return "TwoHandler";
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        String key = sekiroRequest.getString("key");
        JSONObject jsonObject = new JSONObject();
        boolean result = Store.callRun(key);
        jsonObject.put("key", result);
        sekiroResponse.success(jsonObject);
    }
}
