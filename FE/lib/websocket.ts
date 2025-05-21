import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Flag to disable WebSockets during development
const WEBSOCKETS_ENABLED = true; // Set to true when WebSocket server is ready and you need it

let stompClient: Client | null = null;
let orderSubscription: any = null;
let connectionAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 2;

export const connectWebSocket = (
  channelId: string,
  onOrderReceived: () => void, // chỉ cần callback trigger
) => {
  // Skip WebSocket connection if disabled
  if (!WEBSOCKETS_ENABLED) {
    console.log('🛑 WebSocket connections are disabled');
    return;
  }
  
  // Don't attempt to reconnect after max attempts
  if (connectionAttempts >= MAX_RECONNECT_ATTEMPTS) {
    console.log('🛑 Maximum reconnection attempts reached. No more attempts.');
    return;
  }
  
  connectionAttempts++;
  const socketUrl = 'http://localhost:6969/grab/ws'; // Update theo backend URL

  stompClient = new Client({
    webSocketFactory: () => new SockJS(socketUrl),
    debug: (str: string) => console.log('STOMP Debug: ', str),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,

    onConnect: () => {
      console.log('✅ Connected to WebSocket');
      const topic = `/topic/${channelId}`;

      if (orderSubscription) {
        orderSubscription.unsubscribe();
      }

      orderSubscription = stompClient?.subscribe(topic, (_message: IMessage) => {
        console.log('🔔 Notification received (no parsing needed)');
        onOrderReceived(); // Gọi callback mà không parse gì cả
      });
    },

    onDisconnect: () => {
      console.log('⚠️ Disconnected from WebSocket');
    },

    onStompError: (frame) => {
      console.error('❗ Broker error:', frame.headers['message']);
      console.error('❗ Details:', frame.body);
    },

    onWebSocketError: (event) => {
      console.error('❗ WebSocket error', event);
    },
  });

  stompClient.activate();
};

export const disconnectWebSocket = () => {
  // No-op if WebSockets are disabled
  if (!WEBSOCKETS_ENABLED) {
    return;
  }
  
  if (stompClient) {
    try {
      if (orderSubscription) {
        orderSubscription.unsubscribe();
        orderSubscription = null;
      }
      stompClient.deactivate();
      console.log('❎ WebSocket disconnected');
    } catch (error) {
      console.error('Error disconnecting WebSocket:', error);
    }
    stompClient = null;
  }
  
  // Reset the connection attempts counter
  connectionAttempts = 0;
};
