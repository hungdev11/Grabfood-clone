package com.grabdriver.myapplication.services;

import android.content.Context;

import com.grabdriver.myapplication.models.AcceptOrderRequest;
import com.grabdriver.myapplication.models.ApiResponse;
import com.grabdriver.myapplication.models.DriverOrderResponse;
import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.models.OrderResponse;
import com.grabdriver.myapplication.models.RejectOrderRequest;
import com.grabdriver.myapplication.models.UpdateStatusRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class OrderRepository extends ApiRepository {

    public OrderRepository(Context context) {
        super(context);
    }

    // Lấy danh sách đơn hàng có sẵn
    public void getAvailableOrders(int page, int size, NetworkCallback<OrderResponse> callback) {
        Call<ApiResponse<List<DriverOrderResponse>>> call = getApiService().getAvailableOrders(page, size);
        executeCall(call, new NetworkCallback<List<DriverOrderResponse>>() {
            @Override
            public void onSuccess(List<DriverOrderResponse> driverOrders) {
                // Convert DriverOrderResponse to Order for backward compatibility
                List<Order> orders = new ArrayList<>();
                if (driverOrders != null) {
                    for (DriverOrderResponse driverOrder : driverOrders) {
                        if (driverOrder != null) {
                            orders.add(driverOrder.toOrder());
                        }
                    }
                }
                
                OrderResponse response = new OrderResponse();
                response.setOrders(orders);
                response.setCurrentPage(page);
                response.setTotalItems(orders.size());
                callback.onSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Lấy danh sách đơn hàng đã được giao
    public void getAssignedOrders(int page, int size, NetworkCallback<OrderResponse> callback) {
        Call<ApiResponse<List<DriverOrderResponse>>> call = getApiService().getAssignedOrders(page, size);
        executeCall(call, new NetworkCallback<List<DriverOrderResponse>>() {
            @Override
            public void onSuccess(List<DriverOrderResponse> driverOrders) {
                // Convert DriverOrderResponse to Order for backward compatibility
                List<Order> orders = new ArrayList<>();
                if (driverOrders != null) {
                    for (DriverOrderResponse driverOrder : driverOrders) {
                        if (driverOrder != null) {
                            orders.add(driverOrder.toOrder());
                        }
                    }
                }
                
                OrderResponse response = new OrderResponse();
                response.setOrders(orders);
                response.setCurrentPage(page);
                response.setTotalItems(orders.size());
                callback.onSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Lấy lịch sử đơn hàng
    public void getOrderHistory(int page, int size, NetworkCallback<OrderResponse> callback) {
        Call<ApiResponse<List<DriverOrderResponse>>> call = getApiService().getOrderHistory(page, size, new HashMap<>());
        executeCall(call, new NetworkCallback<List<DriverOrderResponse>>() {
            @Override
            public void onSuccess(List<DriverOrderResponse> driverOrders) {
                // Convert DriverOrderResponse to Order for backward compatibility
                List<Order> orders = new ArrayList<>();
                if (driverOrders != null) {
                    for (DriverOrderResponse driverOrder : driverOrders) {
                        if (driverOrder != null) {
                            orders.add(driverOrder.toOrder());
                        }
                    }
                }
                
                OrderResponse response = new OrderResponse();
                response.setOrders(orders);
                response.setCurrentPage(page);
                response.setTotalItems(orders.size());
                callback.onSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Lấy lịch sử đơn hàng với bộ lọc
    public void getOrderHistory(int page, int size, Map<String, String> filters, NetworkCallback<OrderResponse> callback) {
        Call<ApiResponse<List<DriverOrderResponse>>> call = getApiService().getOrderHistory(page, size, filters);
        executeCall(call, new NetworkCallback<List<DriverOrderResponse>>() {
            @Override
            public void onSuccess(List<DriverOrderResponse> driverOrders) {
                // Convert DriverOrderResponse to Order for backward compatibility
                List<Order> orders = new ArrayList<>();
                if (driverOrders != null) {
                    for (DriverOrderResponse driverOrder : driverOrders) {
                        if (driverOrder != null) {
                            orders.add(driverOrder.toOrder());
                        }
                    }
                }
                
                OrderResponse response = new OrderResponse();
                response.setOrders(orders);
                response.setCurrentPage(page);
                response.setTotalItems(orders.size());
                callback.onSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Lấy chi tiết đơn hàng
    public void getOrderDetails(long orderId, NetworkCallback<Order> callback) {
        Call<ApiResponse<DriverOrderResponse>> call = getApiService().getOrderDetails(orderId);
        executeCall(call, new NetworkCallback<DriverOrderResponse>() {
            @Override
            public void onSuccess(DriverOrderResponse driverOrder) {
                if (driverOrder != null) {
                    callback.onSuccess(driverOrder.toOrder());
                } else {
                    callback.onError("Order not found");
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    // Chấp nhận đơn hàng
    public void acceptOrder(long orderId, String pickupTime, String deliveryTime, String note, NetworkCallback<Order> callback) {
        AcceptOrderRequest request = new AcceptOrderRequest(pickupTime, deliveryTime, note);
        Call<ApiResponse<Order>> call = getApiService().acceptOrder(orderId, request);
        executeCall(call, callback);
    }
    
    // Từ chối đơn hàng
    public void rejectOrder(long orderId, String reason, String note, NetworkCallback<Void> callback) {
        RejectOrderRequest request = new RejectOrderRequest(reason, note);
        Call<ApiResponse<Void>> call = getApiService().rejectOrder(orderId, request);
        executeCall(call, callback);
    }
    
    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(long orderId, String status, String note, NetworkCallback<Order> callback) {
        UpdateStatusRequest request = new UpdateStatusRequest(note);
        Call<ApiResponse<Order>> call = getApiService().updateOrderStatus(orderId, status, request);
        executeCall(call, callback);
    }
    
    // Xác nhận đã lấy hàng
    public void confirmPickup(long orderId, NetworkCallback<Order> callback) {
        Call<ApiResponse<Order>> call = getApiService().confirmPickup(orderId);
        executeCall(call, callback);
    }
    
    // Xác nhận đã giao hàng
    public void confirmDelivery(long orderId, NetworkCallback<Order> callback) {
        Call<ApiResponse<Order>> call = getApiService().confirmDelivery(orderId);
        executeCall(call, callback);
    }
    
    // Lấy số đơn hàng đang chờ
    public void getPendingOrdersCount(NetworkCallback<Integer> callback) {
        Call<ApiResponse<Integer>> call = getApiService().getPendingOrdersCount();
        executeCall(call, callback);
    }
} 