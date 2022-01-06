package ggsbot.service;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {

    private List<Integer> availableRadius;

    private List<String> availablePointClasses;

    private final ClientService clientService;

    @Autowired
    public SettingsService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Value("${search.radius}")
    public void setAvailableRadius(List<Integer> availableRadius) {
        this.availableRadius = availableRadius;
    }

    @Value("${point.classes}")
    public void setPointClasses(List<String> pointClasses) {
        this.availablePointClasses = pointClasses;
    }

    public SendMessage initInlineKeyboard(Client client) {
        SendMessage sm = new SendMessage();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        initRadiusKeyBoard(rows, client);
        initAvailablePointClasses(rows, client);
        initAvailableFileFormats(rows, client);
        initActionButtons(rows, client);
        markup.setKeyboard(rows);
        sm.setText(Messages.SETTINGS_MESSAGE);
        sm.setReplyMarkup(markup);
        sm.setChatId(String.valueOf(client.getId()));
        return sm;
    }

    private void initRadiusKeyBoard(List<List<InlineKeyboardButton>> rows, Client client) {
        for (int i = 0; i < availableRadius.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(row);
            int savedClientRadius = clientService.getSavedPointRadius(client);
            String msg;
            if (availableRadius.get(i) != savedClientRadius) {
                msg = Messages.EMPTY_SYMBOL.concat(String.valueOf(availableRadius.get(i)));
            } else {
                msg = Messages.CONFIRM_SYMBOL.concat(String.valueOf(availableRadius.get(i)));
            }
            row.add(newButton(msg + " км", msg));
        }
    }

    private void initAvailablePointClasses(List<List<InlineKeyboardButton>> rows, Client client) {
        for (int i = 0; i < availablePointClasses.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(row);
            List<String> savedClientClasses = clientService.getSavedClientPointClasses(client);
            String msg;
            if (savedClientClasses.contains(availablePointClasses.get(i))) {
                msg = Messages.CONFIRM_SYMBOL.concat(String.valueOf(availablePointClasses.get(i)));
            } else {
                msg = Messages.EMPTY_SYMBOL.concat(String.valueOf(availablePointClasses.get(i)));
            }
            row.add(newButton(msg + " класс", msg));
        }
    }

    private void initAvailableFileFormats(List<List<InlineKeyboardButton>> rows, Client client) {
        FileFormat[] fileFormats = FileFormat.values();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < fileFormats.length; i++) {
            List<String> savedFileFormats = clientService.getSavedClientFileFormats(client);
            String msg;
            if (savedFileFormats.contains(fileFormats[i].name())) {
                msg = Messages.CONFIRM_SYMBOL.concat(String.valueOf(fileFormats[i]));
            } else {
                msg = Messages.EMPTY_SYMBOL.concat(String.valueOf(fileFormats[i]));
            }
            row.add(newButton(msg, msg));
        }
        rows.add(row);
    }

    private void initActionButtons(List<List<InlineKeyboardButton>> rows, Client client) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(newButton(Messages.DEFAULT, Messages.DEFAULT));
        row.add(newButton(Messages.APPROVE, Messages.APPROVE));
        rows.add(row);
    }

    private InlineKeyboardButton newButton(String msg, String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(msg);
        button.setCallbackData(callback);
        return button;
    }
}
