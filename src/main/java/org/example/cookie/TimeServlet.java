package org.example.cookie;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    private String initTime;
    private String correctTimezone;

    @Override
    public void init() throws ServletException{
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String timezone = req.getParameter("timezone");
        Cookie cookie;
        if ( timezone != null &&  !timezone.isEmpty()) {
            correctTimezone = timezone.strip().replaceAll(" ", "+");
            initTime = timeFormatter(LocalDateTime.now(ZoneId.of(correctTimezone)));
            cookie = new Cookie("lastTimezone", correctTimezone);
            cookie.setMaxAge(24 * 60);
            resp.addCookie(cookie);
        }
        else {
            Cookie[] cookies = req.getCookies();

                if(cookies != null){
                    for (Cookie cook: cookies) {
                        if (cook.getName().equals("lastTimezone")) {
                            correctTimezone = cook.getValue();
                            break;
                        }
                    }
                }

                if (correctTimezone==null || correctTimezone.isEmpty()) {
                    initTime = timeFormatter(LocalDateTime.now());
                }
        }

        Map<String, Object> param = new LinkedHashMap<>();
        param.put("time", timeFormatter(LocalDateTime.now(ZoneId.of(correctTimezone))));
        param.put ("timezone", correctTimezone);

        Context simpleContext = new Context( req.getLocale(), param);
        engine.process("time", simpleContext, resp.getWriter());
            resp.getWriter().close();
    }

    private String timeFormatter (LocalDateTime initTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return initTime.format(formatter);
    }

}
