package ggsbot.constants;

public class Messages {

    public static final String SEND_LOCATION = "Прикрепите геолокацию (или отправьте широта;долгота или поделитесь точкой из яндекс карт)";

    public static final String START = "/start";

    public static final String SETTINGS = "Настройки";

    public static final String SETTINGS_MESSAGE = "Выберите радиус поиска, необходимые классы точек и формат выходного файла";

    public static final String EMPTY_SYMBOL = "🔳 ";

    public static final String CONFIRM_SYMBOL = "✅ ";

    public static final String APPROVE = "📝 Подтвердить";

    public static final String DEFAULT = "⚙️ Сбросить к заводским";

    public static final String SETTINGS_APPROVE = "Настройки применены";

    public static final String SETTINGS_DEFAULT = "Настройки сброшены к заводским";

    public static final String FILES_READY = "Файлы сформированы";

    public static final String POINTS_NOT_FOUND = "Не найдено ни одной точки в заданном радиусе";

    public static final String WRONG_TEXT_COORDS = "Неправильный формат ввода. Широта и долгота разделяются точкой с запятой.\n" +
            "Пример: 55.752102; 37.623224";

    public static final String WRONG_YANDEX_MAP_LINK =
            "Невалидная ссылка. Убедитесь что вы делитесь координатами из приложения Яндекс карты или используйте другой метод отправки координат.";

    public static final String INCORRECT_INPUT = "Отправленные координаты не соответсвуют ни одному из поддерживаемых методов.";

    public static final String INCORRECT_INPUT_SETTINGS = "Вы находитесь в разделе настроек.\nДля продолжения работы их нужно подтвердить.";
}
