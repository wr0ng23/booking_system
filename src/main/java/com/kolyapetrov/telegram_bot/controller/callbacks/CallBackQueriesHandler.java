package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class CallBackQueriesHandler {
    private final Map<CallBackName, CallBackHandler> callBackHandlerMap;

    public CallBackQueriesHandler(List<CallBackHandler> callBackHandlers) {
        this.callBackHandlerMap = callBackHandlers.stream().collect(toMap(CallBackHandler::getCallBack, identity()));
    }

    public CallBackHandler retrieveCallBack(CallBackName callBackName) {
        return callBackHandlerMap.getOrDefault(callBackName, null);
    }
}
