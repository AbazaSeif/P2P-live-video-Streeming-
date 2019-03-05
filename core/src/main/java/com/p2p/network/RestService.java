package com.p2p.network;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import com.p2p.exceptions.CoreException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;

@Component
public abstract class RestService {

    private static final Logger LOG = Logger.getLogger(RestService.class);
    private Retrofit restClient;
    @PostConstruct
    private void initializeRestService() {
        this.restClient = getNetworkService().getRetrofit();
        initializeService();
    }

    /**
     * Gets the rest client.
     */
    protected Retrofit getRestClient() {
        return restClient;
    }

    /**
     * Execute request.
     */
    protected <T> retrofit2.Response<T> executeRequest(Call<T> request) {
        try {
            retrofit2.Response<T> response = request.execute();
            if (!response.isSuccessful()) {
                throw new CoreException.NotValidException(response.errorBody().string());
            }
            return response;
        } catch (IOException ie) {
            throw new CoreException.NotValidException(ie.getMessage(), ie);
        }
    }

    /**
     * The Class Request.
     */
    public abstract static class Request implements Serializable {
        private static final long serialVersionUID = 3333754577380927183L;
    }


    /**
     * The Class Response.
     */
    public abstract static class Response implements Serializable {
        private static final long serialVersionUID = 423581841265636152L;
    }

    /**
     * Initialize service.
     */
    protected abstract void initializeService();

    /**
     * Gets the network service.
     */
    protected abstract NetworkService getNetworkService();
}
