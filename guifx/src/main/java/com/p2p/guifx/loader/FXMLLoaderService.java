package com.p2p.guifx.loader;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("singleton")
public class FXMLLoaderService {

    private static final Logger LOG = Logger.getLogger(FXMLLoaderService.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @PostConstruct
    private void postConstruct() {
        LOG.debug("PostConstruct: set up " + getClass().getName());
    }

    public FXMLLoader getLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });
        return loader;
    }

    public FXMLLoader load(String file) {
        return getLoader(getClass().getResource("/view/components/" + file));
    }

    public FXMLLoader getLoader(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });
        return loader;
    }

    public FXMLLoader getLoader(URL location, ResourceBundle resourceBundle) {
        FXMLLoader loader = new FXMLLoader(location, resourceBundle);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return context.getBean(param);
            }
        });
        return loader;
    }

    @PreDestroy
    private void preDestroy() {
        LOG.debug("PreDestroy: tear down " + getClass().getName());
    }
}