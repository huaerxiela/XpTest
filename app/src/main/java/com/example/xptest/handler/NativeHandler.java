package com.example.xptest.handler;

import com.example.xptest.HookTest;
import com.example.xptest.Store;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

public class NativeHandler implements ActionHandler {
    @Override
    public String action() {
        return "NativeHandler";
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        int num = sekiroRequest.getIntValue("num");
        int result = HookTest.add1(num, 666);
        sekiroResponse.success(result);
    }
}
