import type { Metadata } from "next";
import "./globals.css";
import { CartProvider } from "./context/CartContext";
import { Toaster } from "sonner";

export const metadata: Metadata = {
  title: "Grabfood App",
  description: "Grabfood App",
  icons: "/logo-grabfood2.svg",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        {children}
        <Toaster />
      </body>
    </html>
  );
}
