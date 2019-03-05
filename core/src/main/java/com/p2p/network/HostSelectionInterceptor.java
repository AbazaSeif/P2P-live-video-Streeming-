package com.p2p.network;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * The type Host selection interceptor.
 */
@Component
public class HostSelectionInterceptor implements Interceptor {
    private volatile String host;

    /**
     * Sets host.
     */
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = this.host;
        if (host != null) {
            URL url = new URL(host);
            HttpUrl newUrl = request.url().newBuilder()
                    .host(url.getHost())
                    .port(url.getPort())
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}