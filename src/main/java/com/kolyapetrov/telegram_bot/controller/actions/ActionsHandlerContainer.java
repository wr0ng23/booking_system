package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class ActionsHandlerContainer {
    private final Map<UserState, ActionHandler> actionHandlerMap;

    public ActionsHandlerContainer(List<ActionHandler> actionHandlerMap) {
        this.actionHandlerMap = actionHandlerMap.stream().collect(toMap(ActionHandler::getState, identity()));
    }

    public ActionHandler retrieveAction(UserState state) {
        return actionHandlerMap.getOrDefault(state, null);
    }
}
