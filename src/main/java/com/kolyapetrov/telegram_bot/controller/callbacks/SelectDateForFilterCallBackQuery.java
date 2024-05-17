package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.SearchAdsFilterService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.kolyapetrov.telegram_bot.util.enums.UserState.ENTER_LOWER_DATE_FOR_FILTER;
import static com.kolyapetrov.telegram_bot.util.enums.UserState.ENTER_UPPER_DATE_FOR_FILTER;

@Component
public class SelectDateForFilterCallBackQuery implements CallBackHandler {
    private final UserService userService;
    private final SearchAdsFilterService searchAdsFilterService;

    @Autowired
    public SelectDateForFilterCallBackQuery(UserService userService, SearchAdsFilterService searchAdsFilterService) {
        this.userService = userService;
        this.searchAdsFilterService = searchAdsFilterService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.SELECT_DATE_FOR_FILTER;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
            AppUser appUser = userInfo.getAppUser();

        LocalDate parsedDate = LocalDate.parse(callBackInfo.getSelectedDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        if (appUser.getUserState() == ENTER_LOWER_DATE_FOR_FILTER) {
                Long recordId = searchAdsFilterService.findRecordIdByUserId(userInfo.getAppUser().getUserId());
                LocalDate upperDate = searchAdsFilterService.findUpperDateById(recordId);

                if ((upperDate != null && upperDate.isBefore(parsedDate)) ||
                        (upperDate != null && upperDate.isEqual(parsedDate))) {
                    sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                            "Выберете начальную дату перед конечной! Текущая конечная дата: " +
                                    upperDate.format(formatter)));
                    return;
                }

                searchAdsFilterService.updateLowerDate(recordId, callBackInfo.getSelectedDate());
                appUser.setUserState(UserState.MAIN);
                userService.saveUser(appUser);
                sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                        "Для поиска выбрана начальная дата: " + parsedDate.format(formatter)));
                DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
                sender.execute(deleteMessage);

            } else if (appUser.getUserState() == ENTER_UPPER_DATE_FOR_FILTER) {
                Long recordId = searchAdsFilterService.findRecordIdByUserId(userInfo.getAppUser().getUserId());
                LocalDate lowerDate = searchAdsFilterService.findLowerDateByUserId(recordId);

                if ((lowerDate != null && lowerDate.isAfter(parsedDate)) ||
                        (lowerDate != null && lowerDate.isEqual(parsedDate))) {
                    sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                            "Выберете конечную дату после начальной! Текущая начальная дата: " +
                                    lowerDate.format(formatter)));
                    return;
                }

                searchAdsFilterService.updateUpperDate(recordId, callBackInfo.getSelectedDate());

                appUser.setUserState(UserState.MAIN);
                userService.saveUser(appUser);
                sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                        "Для поиска выбрана конечная дата: " + parsedDate.format(formatter)));
                DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
                sender.execute(deleteMessage);

            } else {
                sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Фильтр недействителен"));
                DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
                sender.execute(deleteMessage);
            }
    }
}
