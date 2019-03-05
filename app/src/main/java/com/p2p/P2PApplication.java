package com.p2p;

import com.p2p.guifx.SceneController;
import com.p2p.guifx.loader.FXMLLoaderService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.h2.server.web.WebServlet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@SpringBootApplication(scanBasePackages = "com.p2p")
@ImportResource("classpath:spring/application-context.xml")
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class P2PApplication extends Application {

    private static String[] args;
    private static ConfigurableApplicationContext context;

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        return new TomcatEmbeddedServletContainerFactory();
    }

    public static void main(String[] args) throws Exception {
        P2PApplication.args = args;
        context = new SpringApplicationBuilder(P2PApplication.class)
                .headless(false).run(args);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    context.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        FXMLLoaderService loaderService = context.getBean(FXMLLoaderService.class);
        SceneController sceneController = context.getBean(SceneController.class);
        FXMLLoader loader = loaderService.load("mainpage.fxml");
        Parent root = loader.load();
        Scene scene = sceneController.createScene(root);
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
        registration.addUrlMappings("/console/*");
        return registration;
    }
}
