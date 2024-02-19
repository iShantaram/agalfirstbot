package pro.sky.telebot.listener;

import pro.sky.telebot.entity.NotificationTask;
import pro.sky.telebot.repository.NotificationTaskRepository;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                logger.info("Processing update: {}", update);
                Long chatId = update.message().chat().id();
            
                if (update.message().text().equals("/start")) {
                    SendMessage message = new SendMessage(chatId, String.format("Привет, %s! Введи задачу в формате 19.02.2024 22:30 Сделать домашнюю работу", update.message().from().firstName()));
                    telegramBot.execute(message);
                }
            
                Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                Matcher matcher = pattern.matcher(update.message().text());
                String date = null;
                String item = null;
                if (matcher.matches()) {
                    date = matcher.group(1);
                    item = matcher.group(3);
                    logger.info("Date: {}, item: {}", date, item);
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                if (date != null) {
                    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
                    notificationTaskRepository.save(new NotificationTask(chatId, item, dateTime));
                    SendMessage message = new SendMessage(chatId, "Событие сохранено!");
                    telegramBot.execute(message);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
