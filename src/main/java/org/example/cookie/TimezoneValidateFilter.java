package org.example.cookie;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {
        String timezone = req.getParameter("timezone");
        if( timezone != null && !timezone.isEmpty()){
            String correctTimeZone = timezone.strip().replaceAll(" ", "+");
            if (isValidTimezone(correctTimeZone)) {
                chain.doFilter(req, resp);
            } else {
                resp.setStatus(401);
            }
        } else chain.doFilter(req, resp);
    }

    private static boolean isValidTimezone( String timezone) {
        String timezonePattern = "^UTC([+])(?:[0-9]|1[0-9]|2[0-4])$";
        Pattern pattern = Pattern.compile(timezonePattern);
        Matcher matcher = pattern.matcher(timezone);
        return matcher.matches();
    }

}
