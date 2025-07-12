package train.booking.train.booking.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
@RequiredArgsConstructor
@Slf4j
@Component
public class Helper {

    private final TemplateEngine templateEngine;

    public String build(Map<String, Object> m, String mailTemplate) {
        Context context = new Context();
        context.setVariables(m);
        return templateEngine.process(mailTemplate, context);
    }


}
