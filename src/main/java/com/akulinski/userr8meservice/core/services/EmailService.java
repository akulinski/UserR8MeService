package com.akulinski.userr8meservice.core.services;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class EmailService {

  private static final String SUBJECT = "PLEASE ACTIVATE YOUR YOUR ACCOUNT";

  private Configuration cfg;

  private final String url;

  private final String email;

  private final AmazonSimpleEmailService amazonSimpleEmailService;

  public EmailService(@Value("${config.url}") String url, AmazonSimpleEmailService amazonSimpleEmailService,
                      @Value("${config.email}") String email) throws IOException {
    this.url = url;

    this.amazonSimpleEmailService = amazonSimpleEmailService;

    this.email = email;

    cfg = new Configuration(Configuration.VERSION_2_3_29);


    cfg.setDirectoryForTemplateLoading(new File("templates"));

    cfg.setDefaultEncoding("UTF-8");

    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
  }

  @Async
  public void sendMessage(String to, String name, String link, String signature) throws RuntimeException {

    try {
      SendEmailRequest request = new SendEmailRequest()
        .withDestination(
          new Destination().withToAddresses(to))
        .withMessage(new Message()
          .withBody(new Body()
            .withHtml(new Content()
              .withCharset("UTF-8").withData(getEmail(name, url + link, signature)))
            .withText(new Content()
              .withCharset("UTF-8").withData(getEmail(name, url + link, signature))))
          .withSubject(new Content()
            .withCharset("UTF-8").withData(SUBJECT)))

        .withSource(this.email);

      amazonSimpleEmailService.sendEmail(request);
    } catch (IOException | TemplateException e) {
      log.error(e.getMessage());
      throw new IllegalStateException(e.getMessage());
    }
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
