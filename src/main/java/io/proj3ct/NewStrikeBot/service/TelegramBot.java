package io.proj3ct.NewStrikeBot.service;

import com.vdurmont.emoji.EmojiParser;
import io.proj3ct.NewStrikeBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    static final String HELP_TEXT = "ИНСТРУКЦИЯ\n" +
            "команда /start вызывает приветственное сообщение\n\n" +
            "команда /data показывает ваши сохранённые данные\n\n" +
            "команда /deletedata удаляет все ваши сохранённые данные\n\n" +
            "команда /help вызывает инструкции\n\n" +
            "команда /settings вызывает найстройки бота\n\n" +
            "Команда /register (прописывается вручную) вызывает окно регистрации";

    static final String snaryaga = "Добрый день, дорогой новичок!\n" +
            "Существует несколько вариантов снаряжения\n" +
            "Под конкретные роли: штурмовик, разведчик, снайпер, пулеметчик и др.\n" +
            "Но, поскольку ты новичок, посоветую тебе комплект разведчика!\n" +
            "Так как, он легкий и ты сможешь проще освоиться, не отвлекаться\n" +
            "на тяжелое снаряжение и множество его аспектов\n" +
            "Комплект состоит из:\n" +
            "1)Форма(на первое время вы можете использовать комплект ГОРКА).\n" +
            "2)Cамую простую поясную разгрузочную систему(на нее установить подсумки под магазины + сброс)\n" +
            "3)Привод, магазин/магазины к нему, аккумулятор, шары.\n" +
            "4)Обувь(Лучшим вариантом будут берцы, т.к. фиксируют голеностоп)";

    static final String rules = "Вот некоторые из основных правила страйкбола:\n" +
            "1)В игровой зоне запрещено находиться без очков.\n" +
            "2)При выходе из игровой зоны обязательно отомкните магазин от страйкбольного оружия,\n" +
            "сделайте выстрел в сторону (чтобы отстрелить последний шарик) и поставьте оружие на «предохранитель».\n" +
            "3)В страйкболе каждый сам считает попадания в себя. Считается любое попадание в любую часть тела или снаряжения.\n" +
            "4)В зданиях и страйкбольных клубах разрешены привода и страйкбольные пистолеты со скоростью\n" +
            "вылета шарика (весом 0,2 грамма) не более 120 м/с.\n" +
            "5)Хорошим тоном считается, если во время игры подкрасться к человеку сзади\n" +
            "совсем близко, — не стрелять в него, а просто сказать «ты убит» или похлопать по плечу.\n" +
            "6)Использовать и носить в открытом виде страйкбольное оружие можно только в зонах проведения игр,\n" +
            "специализированных клубах и т.д.";

    static final String forest = "1)Самое главное правило в лесу - понижать свой силуэт\n" +
            "2)в качестве укрытий можно использовать: рвы, ямы, окопы, траншеи, стволы деревьев(не работает в боевой обстановке)";

    static final String weapon = "Страйкбольный привод - это Точные копии настоящего оружия, используемые в страйкбольных видах спорта.\n" +
            "Это особый тип маломощного гладкоствольного пневматического оружия, предназначенных для стрельбы неметаллическими сферическими снарядами,\n" +
            "часто в разговорной речи называемыми «шары», которые обычно изготавливаются из пластика или биоразлагаемых полимерных материалов.\n" +
            "Силовые установки для страйкбольного оружия имеют низкую дульную энергию, а пули обладают значительно меньшей пробивной\n" +
            "и останавливающей способностью, чем обычные пневматические пистолеты, и, как правило, безопасны для соревновательных\n" +
            " спортивных и развлекательных целей, если надето соответствующее защитное снаряжение.";
    static final String buildings = "В зданиях работать нужно предельно аккуратно, велик риск получить шаром в лицо с короткой дистанции.\n" +
            "Основное правило, это держаться дальше от угла, где предположительно находится противник, но не пржиматься к стенам, дабы иметь пространство для маневра.\n" +
            "При работе из - за угла, носки ступней должна смотреть в стену, также нужно придерживатиься правила, с какой стороны выглядываешь, то плечо и должно высовываться.\n" +
            "При работе из - за угла, ваш ствол не должен высовываться, чтобы противнику сложнее было вас обнаружить, и не схватить за ствол, если он находится как раз за углом.";



    public TelegramBot (BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "получить приветственное сообщение"));
        listofCommands.add(new BotCommand("/mydata", "получить ваши сохранённые данные"));
        listofCommands.add(new BotCommand("/deletedata", "удалить мои данные"));
        listofCommands.add(new BotCommand("/help", "как пользоваться этим ботом?"));
        listofCommands.add(new BotCommand("/settings", "настройки бота"));
        try{
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e) {
            log.error("ошибка при настройке бота: " + e.getMessage());
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandRecived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "Выбор снаряжения":
                    sendMessage(chatId, snaryaga);
                    break;

                case "Правила страйкбола":
                    sendMessage(chatId, rules);
                    break;

                case "Лес тактика":
                    sendMessage(chatId, forest);
                    break;

                case "Страйкбольное оружие":
                    sendMessage(chatId, weapon);
                    break;

                case "Здания тактика":
                    sendMessage(chatId, buildings);
                    break;

                case "/register":

                    register(chatId);

                    break;

                default:
                    sendMessage(chatId, "Команда не распознана!");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("YES_BUTTON")) {
                String text = "Вы зарегестрированы!";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                }
                catch (TelegramApiException e) {
                    log.error("Error occurred:" + e.getMessage());
                }

            }
        else if (callbackData.equals("NO_BUTTON")) {
                String text = "Регистрация отменена!";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                }
                catch (TelegramApiException e) {
                    log.error("Error occurred:" + e.getMessage());
                }

            }
        }


        
    }

    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData("YES_BUTTON");

        var noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData("NO_BUTTON");

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }

    }

    private void startCommandRecived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Здарова, " + name +", дорогой новичок, ты попал по адресу" + " :skull:");
        //String answer = "Здарова, " + name +", ПОШЛИ ШТУРМОВАТЬ ПОСАДКИ!!!)))";
        log.info("Ответил пользователю " + name);

        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String textToSend)  {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("Правила страйкбола");
        row.add("Страйкбольное оружие");

        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("Лес тактика");
        row.add("Здания тактика");
        row.add("Выбор снаряжения");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }
    }
}

