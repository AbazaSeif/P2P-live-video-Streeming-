package com.p2p.network;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public abstract class NetworkService {

    protected static final Logger LOG = Logger.getLogger(NetworkService.class);
    @Autowired
    @Qualifier("hhObjectMapper")
    protected ObjectMapper objectMapper;
    @Autowired
    protected HostSelectionInterceptor hostSelectionInterceptor;
    protected HttpLoggingInterceptor loggingInterceptor;
    protected OkHttpClient httpClient;
    protected Retrofit retrofit;

    @PostConstruct
    private void initializeNetwork() {
        this.loggingInterceptor = provideHttpLoggingInterceptor();
        this.httpClient = provideOkHttpClient(this.loggingInterceptor, this.hostSelectionInterceptor);
        this.retrofit = provideRetrofit(this.objectMapper, getApiUrl(), this.httpClient);
    }

    /**
     * Provide http logging interceptor.
     */
    protected HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LOG.info(message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return loggingInterceptor;
    }

    /**
     * Provide ok http client.
     */
    protected OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor,
                                               HostSelectionInterceptor hostSelectionInterceptor) {
        OkHttpClient client = new OkHttpClient.Builder().
                addInterceptor(loggingInterceptor).
                addInterceptor(hostSelectionInterceptor)
                .build();
        return client;
    }

    /**
     * Provide retrofit.
     */
    protected Retrofit provideRetrofit(ObjectMapper objectMapper, String baseApiUrl, OkHttpClient httpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseApiUrl).client(httpClient).build();
        return retrofit;
    }

    /**
     * Gets the logging interceptor.
     */
    public HttpLoggingInterceptor getLoggingInterceptor() {
        return loggingInterceptor;
    }

    /**
     * Gets the http client.
     */
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Gets the retrofit.
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * Gets the api url.
     */
    public abstract String getApiUrl();
}
