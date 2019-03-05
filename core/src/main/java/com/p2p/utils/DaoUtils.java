package com.p2p.utils;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DaoUtils {

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public void closeSilently(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // LOG.debug(e.getMessage(), e);
            }
        }
    }
}
