"use client";

import React, { useState, useEffect } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { MapPin, Plus, Trash } from "lucide-react";
import LocationSearch from "@/components/locationSearch";
import { fetchWithAuth } from "@/utils/api";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { da } from "date-fns/locale";

interface Address {
  id: string | number;
  detail: string;
  displayName?: string; // Use this instead of province/district/ward
  default: boolean; // Not isDefault but default
  province?: string; // Make these optional since they might not be in API response
  district?: string;
  ward?: string;
  latitude?: number;
  longitude?: number;
  isDefault?: boolean; // Keep this for compatibility
}

interface AddressRequest {
  province: string;
  district: string;
  ward: string;
  detail: string;
  isDefault: boolean;
  latitude: number;
  longitude: number;
}

export default function DeliveryAddresses() {
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [loading, setLoading] = useState(true);
  const [isAdding, setIsAdding] = useState(false);
  const [hoveredAddressId, setHoveredAddressId] = useState<
    string | number | null
  >(null);
  const [newAddress, setNewAddress] = useState<AddressRequest>({
    province: "",
    district: "",
    ward: "",
    detail: "",
    isDefault: false,
    latitude: 0,
    longitude: 0,
  });

  // Fetch existing addresses
  useEffect(() => {
    async function fetchAddresses() {
      try {
        setLoading(true);
        const userId = localStorage.getItem("grabUserId");
        if (!userId) throw new Error("User ID not found");

        const res = await fetchWithAuth(
          `http://localhost:6969/grab/users/${userId}/addresses`
        );
        if (!res.ok) throw new Error("Failed to fetch addresses");

        const data = await res.json();
        console.log("Address data from API:", data); // More descriptive logging

        // Try different possible data structures
        let addressList = [];
        if (Array.isArray(data)) {
          addressList = data;
        } else if (data.addresses && Array.isArray(data.addresses)) {
          addressList = data.addresses;
        } else if (data.data && Array.isArray(data.data)) {
          addressList = data.data;
        } else {
          addressList = [data]; // Handle single address case
        }

        console.log("Processed address list:", addressList);
        setAddresses(addressList);
      } catch (err) {
        console.error(err);
        toast.error("Không thể tải địa chỉ");
      } finally {
        setLoading(false);
      }
    }
    fetchAddresses();
  }, []);
  // Add this function to handle setting an address as default
  const handleSetDefault = async (id: string | number) => {
    try {
      const userId = localStorage.getItem("grabUserId");
      if (!userId) throw new Error("User ID not found");

      const res = await fetchWithAuth(
        `http://localhost:6969/grab/users/${userId}/addresses/${id}/default`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        }
      );

      if (!res.ok) throw new Error("Failed to update default address");

      // Update local state - set the selected address as default and others as non-default
      setAddresses((prev) =>
        prev.map((addr) => ({
          ...addr,
          default: addr.id === id,
          isDefault: addr.id === id,
        }))
      );

      toast.success("Đã cập nhật địa chỉ mặc định");
    } catch (err) {
      console.error(err);
      toast.error("Không thể cập nhật địa chỉ mặc định");
    }
  };
  // Reverse-geocode to fill fields
  const handleLocationSelect = async (latStr: string, lonStr: string) => {
    try {
      const lat = parseFloat(latStr);
      const lon = parseFloat(lonStr);
      const res = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&addressdetails=1`
      );
      const data = await res.json();
      const addr = data.address || {};

      // Compose detail from house_number + road
      const detailParts: string[] = [];
      if (addr.house_number) detailParts.push(addr.house_number);
      if (addr.road) detailParts.push(addr.road);
      const detail = detailParts.join(" ");

      setNewAddress((prev) => ({
        ...prev,
        province:
          (addr["ISO3166-2-lvl4"] === "VN-SG" ? "TP. Hồ Chí Minh" : "") || "",
        district: addr.city || "", // Partido de Malvinas Argentinas
        ward: addr.suburb || "", // El Triángulo
        detail: addr.house_number
          ? `${addr.house_number} ${addr.road || ""}`
          : addr.road || "",
        latitude: lat,
        longitude: lon,
      }));

      toast.success("Lấy thông tin địa chỉ thành công");
    } catch (err) {
      console.error(err);
      toast.error("Không thể lấy thông tin địa chỉ");
    }
  };

  const handleDetailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewAddress({ ...newAddress, detail: e.target.value });
  };

  const handleDefaultChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewAddress({ ...newAddress, isDefault: e.target.checked });
  };

  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const userId = localStorage.getItem("grabUserId");
      if (!userId) throw new Error("User ID not found");

      // Create a payload that matches what the backend expects
      const payload = {
        detail: newAddress.detail,
        default: newAddress.isDefault,
        latitude: newAddress.latitude,
        longitude: newAddress.longitude,
        // Add these if your API supports them
        province: newAddress.province,
        district: newAddress.district,
        ward: newAddress.ward,
      };

      const res = await fetchWithAuth(
        `http://localhost:6969/grab/users/${userId}/addresses`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );
      if (!res.ok) throw new Error("Failed to add address");

      const saved: Address = await res.json();
      setAddresses((prev) => [...prev, saved]);
      toast.success("Thêm địa chỉ thành công");
      setIsAdding(false);
      setNewAddress({
        province: "",
        district: "",
        ward: "",
        detail: "",
        isDefault: false,
        latitude: 0,
        longitude: 0,
      });
    } catch (err) {
      console.error(err);
      toast.error("Không thể thêm địa chỉ");
    }
  };

  // Update the handleDelete function signature to accept both string and number
  const handleDelete = async (id: string | number) => {
    try {
      const userId = localStorage.getItem("grabUserId");
      if (!userId) throw new Error("User ID not found");

      const res = await fetchWithAuth(
        `http://localhost:6969/grab/users/${userId}/addresses/${id}`,
        { method: "DELETE" }
      );
      if (!res.ok) throw new Error("Failed to delete address");

      setAddresses((prev) => prev.filter((a) => a.id !== id));
      toast.success("Xóa địa chỉ thành công");
    } catch (err) {
      console.error(err);
      toast.error("Không thể xóa địa chỉ");
    }
  };

  if (loading)
    return <div className="text-center py-8">Đang tải địa chỉ...</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-semibold">Địa chỉ giao hàng</h2>
        <Dialog open={isAdding} onOpenChange={setIsAdding}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="h-4 w-4 mr-2" /> Thêm địa chỉ
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Thêm địa chỉ mới</DialogTitle>
            </DialogHeader>
            <form onSubmit={handleAddAddress} className="space-y-4 pt-4">
              <div>
                <Label>Chọn vị trí</Label>
                <LocationSearch onSelectLocation={handleLocationSelect} />
              </div>

              <div>
                <Label htmlFor="detail">Chi tiết địa chỉ</Label>
                <Input
                  id="detail"
                  value={newAddress.detail}
                  onChange={handleDetailChange}
                  placeholder="Số nhà, tên đường"
                  required
                />
              </div>

              <div className="flex items-center">
                <input
                  id="isDefault"
                  type="checkbox"
                  checked={newAddress.isDefault}
                  onChange={handleDefaultChange}
                  className="mr-2"
                />
                <Label htmlFor="isDefault">Đặt làm mặc định</Label>
              </div>

              <div className="flex justify-end space-x-2">
                <Button
                  variant="outline"
                  type="button"
                  onClick={() => setIsAdding(false)}
                >
                  Hủy
                </Button>
                <Button type="submit">Lưu</Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      {addresses.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center p-8">
            <MapPin className="h-12 w-12 text-gray-300 mb-4" />
            <p className="text-gray-500">Chưa có địa chỉ nào.</p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4">
          {addresses.map((addr) => (
            <Card
              key={addr.id}
              className={
                addr.default || addr.isDefault ? "border-green-500" : ""
              }
              onMouseEnter={() => setHoveredAddressId(addr.id)}
              onMouseLeave={() => setHoveredAddressId(null)}
            >
              <CardContent className="flex justify-between items-center p-4">
                <div>
                  <p className="font-medium">{addr.detail}</p>
                  <p className="text-sm">
                    {addr.displayName ||
                      [addr.ward, addr.district, addr.province]
                        .filter(Boolean)
                        .join(", ") ||
                      "Không có thông tin địa chỉ"}
                  </p>
                </div>
                <div className="flex items-center space-x-2">
                  {addr.default || addr.isDefault ? (
                    <span className="text-green-600 text-sm">Mặc định</span>
                  ) : (
                    // Only show "Set as default" button when hovering
                    hoveredAddressId === addr.id && (
                      <Button
                        variant="outline"
                        size="sm"
                        className="text-green-600 border-green-600 hover:bg-green-50"
                        onClick={() => handleSetDefault(addr.id)}
                      >
                        Đặt làm mặc định
                      </Button>
                    )
                  )}
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={() => handleDelete(addr.id)}
                  >
                    <Trash className="h-4 w-4 text-red-500" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
