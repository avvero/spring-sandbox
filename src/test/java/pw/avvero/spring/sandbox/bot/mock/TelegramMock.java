package pw.avvero.spring.sandbox.bot.mock;

import org.springframework.test.web.client.response.DefaultResponseCreator;
import pw.avvero.test.http.RequestCaptor;

public interface TelegramMock {
    RequestCaptor sendMessage(DefaultResponseCreator responseCreator);
}
