package com.binance.api.client.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.binance.api.client.BinanceApiError;
import com.binance.api.client.BinanceApiHttpInterceptor;
import com.binance.api.client.config.BinanceApiConfig;
import com.binance.api.client.exception.BinanceApiException;
import com.binance.api.client.security.AuthenticationInterceptor;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Generates a Binance API implementation based on @see {@link BinanceApiService}.
 */
public class BinanceApiServiceGenerator {

    private static final OkHttpClient sharedClient;
    private static final Converter.Factory converterFactory = JacksonConverterFactory.create();

    private static List<BinanceApiHttpInterceptor> interceptorsHttp = null;
    
	
    static {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
    }
    
    private BinanceApiServiceGenerator() {
    	
    }

    @SuppressWarnings("unchecked")
    private static final Converter<ResponseBody, BinanceApiError> errorBodyConverter =
            (Converter<ResponseBody, BinanceApiError>)converterFactory.responseBodyConverter(
                    BinanceApiError.class, new Annotation[0], null);

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    /**
     * Create a Binance API service.
     *
     * @param serviceClass the type of service.
     * @param apiKey Binance API key.
     * @param secret Binance secret.
     *
     * @return a new implementation of the API endpoints for the Binance API service.
     */
    public static <S> S createService(Class<S> serviceClass, String apiKey, String secret, List<BinanceApiHttpInterceptor> interceptorsHttp) {
        String baseUrl = null;
        if (!BinanceApiConfig.useTestnet) { 
        	baseUrl = BinanceApiConfig.getApiBaseUrl(); 
        }
        else {
            baseUrl = BinanceApiConfig.getTestNetBaseUrl();
        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory);

        if (StringUtils.isEmpty(apiKey) || StringUtils.isEmpty(secret)) {
            retrofitBuilder.client(sharedClient);
        } else {
            // `adaptedClient` will use its own interceptor, but share thread pool etc with the 'parent' client
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(apiKey, secret);
            OkHttpClient adaptedClient = sharedClient.newBuilder().addInterceptor(interceptor).build();
            retrofitBuilder.client(adaptedClient);
        }
        
        if (interceptorsHttp != null) {
        	BinanceApiServiceGenerator.interceptorsHttp = interceptorsHttp;
        }

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }
    

    /**
     * Create a Binance API service.
     *
     * @param serviceClass the type of service.
     * @param apiKey Binance API key.
     * @param secret Binance secret.
     *
     * @return a new implementation of the API endpoints for the Binance API service.
     */
    public static <S> S createService(Class<S> serviceClass, String apiKey, String secret) {
        return BinanceApiServiceGenerator.createService(serviceClass, apiKey, secret, null);
    }    
    
    /**
     * Execute a REST call and block until the response is received.
     */
    public static <T> T executeSync(Call<T> call, boolean flagTraceLogRequest, boolean flagTraceLogResponse) {
        try {
        	BinanceApiServiceGenerator.executeInterceptorBeforeRequest(call, flagTraceLogRequest);
            Response<T> response = call.execute();
        	BinanceApiServiceGenerator.executeInterceptorAfterResponse(response, flagTraceLogResponse);
        	
            if (response.isSuccessful()) {           	
                return response.body();
            } else {
                BinanceApiError apiError = getBinanceApiError(response);
                throw new BinanceApiException(apiError);
            }
        } catch (IOException e) {
        	System.out.println("Error al invocar la API. Motivo: " + e.getMessage());
        	e.printStackTrace();
            throw new BinanceApiException(e);
        }
    }
        

    /**
     * Execute a REST call and block until the response is received.
     */
    public static <T> T executeSync(Call<T> call) {
    	return BinanceApiServiceGenerator.executeSync(call, true, true);
    }
    
    private static <T> void  executeInterceptorBeforeRequest(Call<T> call, boolean flagTraceLogRequest) {
    	if (BinanceApiServiceGenerator.interceptorsHttp != null && !BinanceApiServiceGenerator.interceptorsHttp.isEmpty()) {
    		for (BinanceApiHttpInterceptor interceptorHttp : BinanceApiServiceGenerator.interceptorsHttp) {
    			try {
    			    interceptorHttp.interceptBeforeRequest(call, flagTraceLogRequest);
    			} catch (Exception ex) {
    				ex.printStackTrace();
    			}
    		}
    	}
    }
    
    private static <T> void  executeInterceptorAfterResponse(Response<T> response, boolean flagTraceLogResponse) {
    	if (BinanceApiServiceGenerator.interceptorsHttp != null && !BinanceApiServiceGenerator.interceptorsHttp.isEmpty()) {
    		for (BinanceApiHttpInterceptor interceptorHttp : BinanceApiServiceGenerator.interceptorsHttp) {
    			try {
        			interceptorHttp.interceptAfterResponse(response, flagTraceLogResponse);
    			} catch (Exception ex) {
    				ex.printStackTrace();
    			}    			
    		}
    	}
    }    
    
	
    /**
     * Extracts and converts the response error body into an object.
     */
    public static BinanceApiError getBinanceApiError(Response<?> response) throws IOException {    	
        return errorBodyConverter.convert(response.errorBody());
    }

    /**
     * Returns the shared OkHttpClient instance.
     */
    public static OkHttpClient getSharedClient() {
        return sharedClient;
    }
}
