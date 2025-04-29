"use client";

import { CartProvider } from "../context/CartContext";
import { Toaster } from "sonner";

export default function AccountLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <CartProvider>
      {children}
      <Toaster position="top-center" richColors duration={4000} closeButton />
    </CartProvider>
  );
}
