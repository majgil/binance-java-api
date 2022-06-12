package com.binance.api.client.domain.account.request;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response object returned when an order is canceled.
 *
 * @see CancelOrderRequest for the request
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelOrderResponse {

  private String symbol;

  private String origClientOrderId;

  private Long orderId;

  private String clientOrderId;

  private OrderStatus status;

  private String executedQty;
  
  private String price;
  
  private String stopPrice;  
  
  private String origQty;
  
  private String cummulativeQuoteQty;
  
  private OrderType type;
  
  private OrderSide side;      

  public String getSymbol() {
    return symbol;
  }

  public CancelOrderResponse setSymbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  public String getOrigClientOrderId() {
    return origClientOrderId;
  }

  public CancelOrderResponse setOrigClientOrderId(String origClientOrderId) {
    this.origClientOrderId = origClientOrderId;
    return this;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setExecutedQty(String executedQty) {
    this.executedQty = executedQty;
  }

  public String getExecutedQty() {
    return executedQty;
  }

  public Long getOrderId() {
    return orderId;
  }

  public CancelOrderResponse setOrderId(Long orderId) {
    this.orderId = orderId;
    return this;
  }

  public String getClientOrderId() {
    return clientOrderId;
  }

  public CancelOrderResponse setClientOrderId(String clientOrderId) {
    this.clientOrderId = clientOrderId;
    return this;
  }
  
  

    public String getPrice() {
	   return price;
    }

	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getStopPrice() {
		return stopPrice;
	}
	
	public void setStopPrice(String stopPrice) {
		this.stopPrice = stopPrice;
	}
	
	public String getOrigQty() {
		return origQty;
	}
	
	public void setOrigQty(String origQty) {
		this.origQty = origQty;
	}
	
	public String getCummulativeQuoteQty() {
		return cummulativeQuoteQty;
	}
	
	public void setCummulativeQuoteQty(String cummulativeQuoteQty) {
		this.cummulativeQuoteQty = cummulativeQuoteQty;
	}
	
	public OrderType getType() {
		return type;
	}
	
	public void setType(OrderType type) {
		this.type = type;
	}
	
	public OrderSide getSide() {
		return side;
	}
	
	public void setSide(OrderSide side) {
		this.side = side;
	}

	@Override
	public String toString() {
		return "CancelOrderResponse [symbol=" + symbol + ", origClientOrderId=" + origClientOrderId + ", orderId="
				+ orderId + ", clientOrderId=" + clientOrderId + ", status=" + status + ", executedQty=" + executedQty
				+ ", price=" + price + ", stopPrice=" + stopPrice + ", origQty=" + origQty + ", cummulativeQuoteQty="
				+ cummulativeQuoteQty + ", type=" + type + ", side=" + side + "]";
	}


	
	
	
}