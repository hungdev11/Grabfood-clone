import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient: Client | null = null;
let orderSubscription: any = null;

export const connectWebSocket = (
  channelId: string,
  onOrderReceived: () => void, // chỉ cần callback trigger
) => {
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
  if (stompClient) {
    if (orderSubscription) {
      orderSubscription.unsubscribe();
    }
    stompClient.deactivate();
    console.log('❎ WebSocket disconnected');
  }
};
