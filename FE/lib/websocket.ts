import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient: Client | null = null;
let orderSubscription: any = null;

export const connectWebSocket = (
  onOrderReceived: (order: any) => void,
) => {
  const socket = new SockJS('http://localhost:6969/grab/ws'); // Backend WebSocket URL

  stompClient = new Client({
    webSocketFactory: () => socket,
    debug: (str: string) => {
      console.log('STOMP Debug: ', str); // Log debug để kiểm tra kết nối
    },
    onConnect: () => {
      console.log('Connected to WebSocket'); // Log khi kết nối thành công

      // Subcribe đơn hàng mới
      orderSubscription = stompClient?.subscribe('/topic/restaurant/1', (message: IMessage) => {
        // Kiểm tra nếu bạn nhận được chuỗi văn bản, không cần phải parse
        const orderMessage = message.body; // Đây là chuỗi văn bản, không phải JSON
        console.log('Received order message:', orderMessage); // Để kiểm tra
        onOrderReceived(orderMessage); // Gọi callback
    });
    
    },
    onDisconnect: () => {
      console.log('Disconnected from WebSocket'); // Log khi mất kết nối
    },
  });

  stompClient.activate();
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    if (orderSubscription) {
      orderSubscription.unsubscribe(); // Ngừng subscribe khi không cần nữa
    }
    stompClient.deactivate();
    console.log('WebSocket disconnected'); // Log khi kết nối bị hủy
  }
};
