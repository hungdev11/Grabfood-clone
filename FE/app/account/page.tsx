"use client";

import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import PersonalInformation from "@/components/PersonalInformation";
import DeliveryAddresses from "@/components/DeliveryAddresses";
import Header from "@/components/header";
import Footer from "@/components/footer";
import { User, MapPin, ShoppingBag } from "lucide-react";
import { useRouter } from "next/navigation";
import { CartProvider } from "../context/CartContext";


export default function AccountPage() {
  const [activeTab, setActiveTab] = useState("personal-info");
  const router = useRouter();
  const handleTabChange = (value: string) => {
    if (value === "my-orders") {
      router.push("/order-history");
    } else {
      setActiveTab(value);
    }
  };


  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="mx-auto w-full max-w-7xl px-4 py-8">
        <h1 className="mb-6 text-2xl font-bold">Tài khoản của tôi</h1>

        <Tabs value={activeTab} onValueChange={handleTabChange} className="w-full">
          <TabsList className="mb-6 grid w-full grid-cols-3">
            <TabsTrigger
              value="personal-info"
              className="flex items-center gap-2"
            >
              <User className="h-4 w-4" />
              <span>Thông tin cá nhân</span>
            </TabsTrigger>
            <TabsTrigger
              value="delivery-addresses"
              className="flex items-center gap-2"
            >
              <MapPin className="h-4 w-4" />
              <span>Địa chỉ giao hàng</span>
            </TabsTrigger>
            <TabsTrigger value="my-orders" className="flex items-center gap-2">
              <ShoppingBag className="h-4 w-4" />
              <span>Đơn hàng của tôi</span>
            </TabsTrigger>
          </TabsList>

          <TabsContent value="personal-info">
            <PersonalInformation />
          </TabsContent>

          <TabsContent value="delivery-addresses">
            <DeliveryAddresses />
          </TabsContent>
          
          {/* No TabsContent for my-orders since we're navigating away */}
        </Tabs>
      </div>
      <Footer />
    </div>
  );
}
