package com.binance.api.client.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.binance.api.client.BinanceApiError;
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

	public static final String FORMAT_DATE_HOUR_MIN_SEC_ISO = "yyyy-MM-dd'T'HH:mm:ss.S";
	public static long COUNT_REQUEST = 0;	
	
    static {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
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
    public static <S> S createService(Class<S> serviceClass, String apiKey, String secret) {
        String baseUrl = null;
        if (!BinanceApiConfig.useTestnet) { baseUrl = BinanceApiConfig.getApiBaseUrl(); }
        else {
            baseUrl = /*BinanceApiConfig.useTestnetStreaming ?
                BinanceApiConfig.getStreamTestNetBaseUrl() :*/
                BinanceApiConfig.getTestNetBaseUrl();
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

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    /**
     * Execute a REST call and block until the response is received.
     */
    public static <T> T executeSync(Call<T> call) {
        try {
        	//COUNT_REQUEST++;
            //System.out.println("API BINANCE. Time ini: " + BinanceApiServiceGenerator.formatToString(new Date(), FORMAT_DATE_HOUR_MIN_SEC_ISO) + ". Nº REQUEST : " + COUNT_REQUEST + ". Data: " + call.request());
            Response<T> response = call.execute();            
            if (response.isSuccessful()) {
            	System.out.println("API BINANCE. Time fin: " + BinanceApiServiceGenerator.formatToString(new Date(), FORMAT_DATE_HOUR_MIN_SEC_ISO)  + ". Nº REQUEST : " + COUNT_REQUEST + ". Resultado OK. Data: "   + response.raw());
                return response.body();
            } else {
                BinanceApiError apiError = getBinanceApiError(response);
            	System.out.println("API BINANCE. Time fin: " + BinanceApiServiceGenerator.formatToString(new Date(), FORMAT_DATE_HOUR_MIN_SEC_ISO)  + ". Nº REQUEST : " + COUNT_REQUEST + ". Resultado KO. Data: "   + response.raw());
                
                throw new BinanceApiException(apiError);
            }
        } catch (IOException e) {
        	System.out.println("Error al invocar la API. Motivo: " + e.getMessage());
        	e.printStackTrace();
            throw new BinanceApiException(e);
        }
    }
    
	
	/**
	 * Transforma una fecha de tipo LocalDate a una String con el formato indicado
	 * 
	 * @param fecha Fecha a transformar
	 * 
	 * @return Representación en String con el formato indicado de la fecha de tipo LocalDate
	 */
	public static String formatToString(Date fecha, String formato) {
		if (fecha == null) {
			return null;
		}
		final SimpleDateFormat dsf = new SimpleDateFormat(formato);
		return dsf.format(fecha);
	}	    

    /**
     * Extracts and converts the response error body into an object.
     */
    public static BinanceApiError getBinanceApiError(Response<?> response) throws IOException, BinanceApiException {
        return errorBodyConverter.convert(response.errorBody());
    }

    /**
     * Returns the shared OkHttpClient instance.
     */
    public static OkHttpClient getSharedClient() {
        return sharedClient;
    }
}
