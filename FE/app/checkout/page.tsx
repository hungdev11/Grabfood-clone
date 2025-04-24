// app/checkout/page.tsx (Next.js 13+)
import Checkout from './CheckoutPage';
import { CartProvider } from '../context/CartContext';

export default function CheckoutPage() {
  return (
    <CartProvider>
      <Checkout />
    </CartProvider>
  );
}
