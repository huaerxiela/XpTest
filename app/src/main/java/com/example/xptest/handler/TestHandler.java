package com.example.xptest.handler;

import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

public class TestHandler implements ActionHandler {
    @Override
    public String action() {
        return "TestHandler";
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        sekiroResponse.success("ok");
    }
}
