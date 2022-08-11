package com.binance.api.client;

import retrofit2.Call;
import retrofit2.Response;

public interface BinanceApiHttpInterceptor {

	public <T> void interceptBeforeRequest(Call<T> call, boolean flagTraceLogRequest);
	
	public <T> void interceptAfterResponse(Response<T> response, boolean flagTraceLogResponse);
}
