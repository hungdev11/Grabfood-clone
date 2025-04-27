import { useEffect } from 'react';
import { connectWebSocket, disconnectWebSocket } from '@/lib/websocket';
import { fr } from 'date-fns/locale';

export const MyComponent = () => {
  // Hàm callback để xử lý đơn hàng nhận được
  const handleOrderReceived = (order: string) => {
    console.log('Order received:', order); // Để kiểm tra log nhận đơn hàng
  };

  useEffect(() => {
    // Kết nối WebSocket khi component mount
    connectWebSocket(handleOrderReceived);

    // Dọn dẹp kết nối WebSocket khi component unmount
    return () => {
      disconnectWebSocket();
    };
  }, []); // Chỉ gọi 1 lần khi component mount

  return <div>WebSocket is connected!</div>;
};
