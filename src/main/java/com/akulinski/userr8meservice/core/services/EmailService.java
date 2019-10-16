package com.akulinski.userr8meservice.core.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class EmailService {

    private String domainName;

    private String apiKey;

    private String from;

    private String address;

    private String port;

    private Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);


    public EmailService(@Value("${config.apikey}") String apiKey, @Value("${config.domain}") String domainName, @Value("${config.from}") String from,@Value("${config.address}") String address,@Value("${config.port}") String port) throws IOException {
        this.apiKey = apiKey;
        this.domainName = domainName;
        this.from = from;
        this.address = address;
        this.port = port;


        cfg.setDirectoryForTemplateLoading(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("templates")).getFile()));

        cfg.setDefaultEncoding("UTF-8");

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    @Async
    public JsonNode sendMessage(String to, String name, String link, String signature) throws UnirestException {

        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.post("https://api.mailgun.net/v3/" + domainName + "/messages")
                    .basicAuth("api", apiKey)
                    .field("from", String.format("KeepMeAwake <%s>", from))
                    .field("to", to)
                    .field("subject", "Activate your account")
                    .field("html", getEmail(name, "localhost:8080/activate/" + link, signature))
                    .asJson();
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage());
        }

        return request.getBody();
    }

    private String getEmail(String name, String link, String signature) throws IOException, TemplateException {
        Template template = cfg.getTemplate("mail.ftl");

        Writer out = new StringWriter();
        template.process(prepareData(name, link, signature), out);

        return out.toString();
    }

    private Map<String, Object> prepareData(String name, String link, String signature) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("link", link);
        data.put("signature", signature);
        return data;
    }


}
