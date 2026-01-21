package ru.aif.aifback.services.process.admin.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin tax type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum AdminTaxType {

    BASE("base",
         "Базовая",
         "\uD83D\uDD36",
         "\uD83D\uDCC5 30 дней: \uD83D\uDCB0 999₽",
         "\uD83D\uDCC5 90 дней: \uD83D\uDCB0 2 699₽",
         "\uD83D\uDCC5 365 дней: \uD83D\uDCB0 9 999₽",
         "Простое решение для тех, кому нужны базовые инструменты управления записью и взаимодействием с клиентами:\n\n" +
         "- <b>Запись:</b> Возможность автоматического приема заявок и запись на услуги прямо внутри мессенджера\n" +
         "- <b>Просмотр записей:</b> Удобный интерфейс для просмотра всех записей и статусов обслуживания\n" +
         "- <b>Повтор записи:</b> Функция повторного бронирования услуги, удобная для постоянных клиентов\n" +
         "- <b>Система оценки услуг:</b> Простой способ сбора обратной связи и улучшения качества ваших сервисов"),
    ADDITIONAL("additional",
               "Расширенная",
               "\uD83D\uDD37",
               "\uD83D\uDCC5 30 дней: \uD83D\uDCB0 1 999₽",
               "\uD83D\uDCC5 90 дней: \uD83D\uDCB0 5 399₽",
               "\uD83D\uDCC5 365 дней: \uD83D\uDCB0 19 999₽",
               "Дополнительно к базовым возможностям открываются новые инструменты повышения лояльности клиентов и удобства их обслуживания:\n\n" +
               "- <b>Все функции базовой подписки</b>\n" +
               "- <b>Рассылка-напоминалка для клиентов:</b> Автоматическое отправление уведомлений клиентам перед запланированными услугами, предотвращение пропусков встреч и улучшение клиентского сервиса\n"
               +
               "- <b>Отдельная вкладка поддержки:</b> автоматический прием жалоб , снижает уровень негатива и неадекватного поведения клиентов"),
    PREMIUM("premium",
            "Премиум",
            "⭐", "\uD83D\uDCC5 30 дней: \uD83D\uDCB0 3 999₽",
            "\uD83D\uDCC5 90 дней: \uD83D\uDCB0 10 999₽",
            "\uD83D\uDCC5 365 дней: \uD83D\uDCB0 40 999₽",
            "Максимальное удобство и персонализация сервиса благодаря интеграции дополнительных функций и индивидуальному подходу:\n\n"
            + "- <b>Подключение к социальным сетям:</b> Синхронизация аккаунта бота с вашими страницами в социальных сетях для приема сообщений из всех соц сетей в 1 чате!\n"
            + "- <b>Написание нового функционала по вашему запросу:</b> Создание уникальных решений именно под ваши потребности и пожелания, повышая эффективность вашего бизнеса");

    private final String type;
    private final String name;
    private final String icon;
    private final String onePrice;
    private final String threePrice;
    private final String twelvePrice;
    private final String description;

    public static AdminTaxType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(BASE);
    }

}
