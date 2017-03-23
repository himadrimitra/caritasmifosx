package org.apache.fineract.infrastructure.security.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This filter is responsible for extracting JsonPost data as request query
 * Parameter for oauth authentication.
 * 
 */
@Service(value="jsonEncodeAuthenticationFilter")
@Profile("oauth")
public class JsonToUrlEncodedAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        boolean toBeProcessed = true;
        if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            if (Objects.equals(request.getContentType(), "application/json;charset=utf-8")
                    && Objects.equals(request.getRequestURI(), "/fineract-provider/api/oauth/token")) {
                InputStream is = request.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] json = buffer.toByteArray();
                if (json.length > 0) {
                    HashMap<String, String> result = new ObjectMapper().readValue(json, HashMap.class);
                    HashMap<String, String[]> r = new HashMap<>();
                    for (String key : result.keySet()) {
                        String[] val = new String[1];
                        val[0] = result.get(key);
                        r.put(key, val);
                    }

                    String[] val = new String[1];
                    val[0] = request.getMethod();
                    r.put("_method", val);

                    if (r.containsKey("password") && r.containsKey("username")) {
                        toBeProcessed = false;
                        HttpServletRequest s = new OauthServletRequestWrapper(request, r);
                        chain.doFilter(s, response);
                    }
                }
            }

        }
        if (toBeProcessed) {
            chain.doFilter(request, response);
        }
    }

}