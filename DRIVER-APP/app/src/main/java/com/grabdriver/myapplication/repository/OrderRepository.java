package com.grabdriver.myapplication.repository;

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
    
    // Lấy danh sách đơn hàng đã được giao theo trạng thái
    public void getAssignedOrdersByStatus(int page, int size, String status, NetworkCallback<OrderResponse> callback) {
        // Sử dụng endpoint getAssignedOrders hiện có và lọc phía client
        Call<ApiResponse<List<DriverOrderResponse>>> call = getApiService().getAssignedOrders(page, size);
        executeCall(call, new NetworkCallback<List<DriverOrderResponse>>() {
            @Override
            public void onSuccess(List<DriverOrderResponse> driverOrders) {
                // Convert DriverOrderResponse to Order for backward compatibility
                List<Order> orders = new ArrayList<>();
                if (driverOrders != null) {
                    for (DriverOrderResponse driverOrder : driverOrders) {
                        if (driverOrder != null) {
                            Order order = driverOrder.toOrder();
                            // Lọc theo trạng thái
                            if ("ALL".equals(status) || status.equals(order.getStatus())) {
                                orders.add(order);
                            }
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
    
    // Lấy số đơn hàng hôm nay
    public void getTodayOrdersCount(NetworkCallback<Integer> callback) {
        // Sử dụng getAssignedOrders để lấy tất cả đơn và filter theo ngày hôm nay
        Call<ApiResponse<List<DriverOrderResponse>>> call = getApiService().getAssignedOrders(1, 100);
        executeCall(call, new NetworkCallback<List<DriverOrderResponse>>() {
            @Override
            public void onSuccess(List<DriverOrderResponse> driverOrders) {
                int todayCount = 0;
                if (driverOrders != null) {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    calendar.set(java.util.Calendar.MINUTE, 0);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);
                    java.util.Date startOfDay = calendar.getTime();
                    
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    java.util.Date startOfNextDay = calendar.getTime();
                    
                    for (DriverOrderResponse driverOrder : driverOrders) {
                        if (driverOrder != null && driverOrder.getOrderDate() != null) {
                            java.util.Date orderDate = driverOrder.getOrderDate();
                            if (orderDate.after(startOfDay) && orderDate.before(startOfNextDay)) {
                                todayCount++;
                            }
                        }
                    }
                }
                callback.onSuccess(todayCount);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
} 