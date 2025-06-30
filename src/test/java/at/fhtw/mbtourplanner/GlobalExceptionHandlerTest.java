import at.fhtw.mbtourplanner.controller.GlobalExceptionHandler;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger log;

    @BeforeEach
    void setUp() {
        log = (Logger) getLogger(GlobalExceptionHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationErrors_logsWarningAndReturnsMap() {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "obj");
        br.addError(new FieldError("obj", "name", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, br);
        var response = handler.handleValidationErrors(ex);

        // Überprüfe Log-Eintrag
        boolean found = listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.WARN &&
                        event.getFormattedMessage().contains("Validation error occurred")
        );
        assertThat(found).isTrue();

        Map<String,String> body = response.getBody();
        assertThat(body).containsEntry("name", "must not be blank");
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void handleNotFound_logsErrorAndReturns404() {
        RuntimeException notFound = new RuntimeException("not found");
        var response = handler.handleNotFound(notFound);

        boolean found = listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.ERROR &&
                        event.getFormattedMessage().contains("Unhandled runtime exception")
        );
        assertThat(found).isTrue();

        Map<String,String> body = response.getBody();
        assertThat(body).containsEntry("error", "not found");
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}