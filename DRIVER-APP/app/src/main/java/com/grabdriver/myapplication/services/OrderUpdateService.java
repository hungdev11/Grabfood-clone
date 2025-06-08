package com.grabdriver.myapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.grabdriver.myapplication.models.Order;
import com.grabdriver.myapplication.utils.SessionManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class OrderUpdateService extends Service {
    private static final long POLLING_INTERVAL = 30000; // 30 seconds

    // Broadcast actions
    public static final String ACTION_NEW_ORDER = "com.grabdriver.NEW_ORDER";
    public static final String ACTION_ORDER_UPDATE = "com.grabdriver.ORDER_UPDATE";
    public static final String ACTION_ORDER_CANCELLED = "com.grabdriver.ORDER_CANCELLED";

    // Extra keys
    public static final String EXTRA_ORDER = "order";
    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_STATUS = "status";

    private SessionManager sessionManager;
    private WebSocketClient webSocketClient;
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private boolean isConnected = false;

    // Interface for order update listeners
    public interface OrderUpdateListener {
        void onNewOrder(Order order);

        void onOrderUpdate(Order order);

        void onOrderCancelled(long orderId);

        void onConnectionStatusChanged(boolean connected);
    }

    private static List<OrderUpdateListener> listeners = new ArrayList<>();

    public static void addOrderUpdateListener(OrderUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeOrderUpdateListener(OrderUpdateListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
        pollingHandler = new Handler(Looper.getMainLooper());


        initializeOrderUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;

        if ("START_ORDER_UPDATES".equals(action)) {
            startOrderUpdates();
        } else if ("STOP_ORDER_UPDATES".equals(action)) {
            stopOrderUpdates();
            stopSelf();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeOrderUpdates() {
        if (sessionManager.isLoggedIn()) {
            // Try WebSocket first, fallback to polling if failed
            if (!initializeWebSocket()) {
                initializePolling();
            }
        }
    }

    private boolean initializeWebSocket() {
        try {
            String wsUrl = "ws://10.0.2.2:6969/grab/ws/driver/" + sessionManager.getShipperId();
            URI serverUri = URI.create(wsUrl);

            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
        
                    isConnected = true;
                    notifyConnectionStatusChanged(true);

                    // Send authentication
                    String authMessage = String.format(
                            "{\"type\":\"auth\",\"token\":\"%s\",\"shipper_id\":%d}",
                            sessionManager.getToken(),
                            sessionManager.getShipperId());
                    send(authMessage);
                }

                @Override
                public void onMessage(String message) {
        
                    handleWebSocketMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
        
                    isConnected = false;
                    notifyConnectionStatusChanged(false);

                    // Try to reconnect after 5 seconds
                    pollingHandler.postDelayed(() -> {
                        if (!isConnected) {
                            initializeWebSocket();
                        }
                    }, 5000);
                }

                @Override
                public void onError(Exception ex) {
        
                    isConnected = false;
                    notifyConnectionStatusChanged(false);

                    // Fallback to polling
                    initializePolling();
                }
            };

            webSocketClient.connect();
            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private void handleWebSocketMessage(String message) {
        try {
            // Parse JSON message and handle order updates

            if (message.contains("\"type\":\"new_order\"")) {
                // Handle new order
                Order order = parseOrderFromMessage(message);
                if (order != null) {
                    handleNewOrder(order);
                }
            } else if (message.contains("\"type\":\"order_update\"")) {
                // Handle order update
                Order order = parseOrderFromMessage(message);
                if (order != null) {
                    handleOrderUpdate(order);
                }
            } else if (message.contains("\"type\":\"order_cancelled\"")) {
                // Handle order cancellation
                long orderId = parseOrderIdFromMessage(message);
                if (orderId > 0) {
                    handleOrderCancelled(orderId);
                }
            }

        } catch (Exception e) {

        }
    }

    private void initializePolling() {


        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn() && sessionManager.isOnline()) {
                    pollForOrderUpdates();
                }

                // Schedule next poll
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };

        // Start polling
        pollingHandler.post(pollingRunnable);
    }

    private void pollForOrderUpdates() {
        // Polling implementation would make API calls to check for order updates
        // This serves as fallback when WebSocket is not available
    }

    private void startOrderUpdates() {

        if (!isConnected) {
            initializeOrderUpdates();
        }
    }

    private void stopOrderUpdates() {


        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }

        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }

        isConnected = false;
        notifyConnectionStatusChanged(false);
    }

    private void handleNewOrder(Order order) {


        // Notify listeners
        for (OrderUpdateListener listener : listeners) {
            listener.onNewOrder(order);
        }

        // Send local broadcast
        Intent intent = new Intent(ACTION_NEW_ORDER);
        intent.putExtra(EXTRA_ORDER, order);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleOrderUpdate(Order order) {


        // Notify listeners
        for (OrderUpdateListener listener : listeners) {
            listener.onOrderUpdate(order);
        }

        // Send local broadcast
        Intent intent = new Intent(ACTION_ORDER_UPDATE);
        intent.putExtra(EXTRA_ORDER, order);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleOrderCancelled(long orderId) {


        // Notify listeners
        for (OrderUpdateListener listener : listeners) {
            listener.onOrderCancelled(orderId);
        }

        // Send local broadcast
        Intent intent = new Intent(ACTION_ORDER_CANCELLED);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyConnectionStatusChanged(boolean connected) {
        for (OrderUpdateListener listener : listeners) {
            listener.onConnectionStatusChanged(connected);
        }
    }

    private Order parseOrderFromMessage(String message) {
        // JSON parsing would be implemented here to create Order object
        return null;
    }

    private long parseOrderIdFromMessage(String message) {
        // JSON parsing would extract order ID from message
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopOrderUpdates();
        listeners.clear();

    }
}