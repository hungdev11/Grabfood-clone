import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient: Client | null = null;
let orderSubscription: any = null;

export const connectWebSocket = (
  channelId: string,
  onOrderReceived: (order: any) => void,
) => {
  const socketUrl = 'http://localhost:6969/grab/ws'; // Update theo backend URL

  stompClient = new Client({
    webSocketFactory: () => new SockJS(socketUrl),
    debug: (str: string) => console.log('STOMP Debug: ', str),
    reconnectDelay: 5000, // Tự động reconnect sau 5s
    heartbeatIncoming: 4000, // Nhận ping mỗi 4s
    heartbeatOutgoing: 4000, // Gửi ping mỗi 4s

    onConnect: () => {
      console.log('✅ Connected to WebSocket');
      const topic = `/topic/${channelId}`;

      // Clear subscription cũ (nếu có)
      if (orderSubscription) {
        orderSubscription.unsubscribe();
      }

      orderSubscription = stompClient?.subscribe(topic, (message: IMessage) => {
        try {
          const orderObject = JSON.parse(message.body);
          console.log('📦 Received order object:', orderObject);
          onOrderReceived(orderObject);
        } catch (e) {
          console.error('❌ Failed to parse order message', e);
        }
      });
    },

    onDisconnect: () => {
      console.log('⚠️ Disconnected from WebSocket');
    },

    onStompError: (frame) => {
      console.error('❗ Broker reported error: ' + frame.headers['message']);
      console.error('❗ Additional details: ' + frame.body);
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
