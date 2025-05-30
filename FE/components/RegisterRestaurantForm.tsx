// components/RegisterRestaurantForm.tsx
import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import LocationSearch from "@/components/locationSearch";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { MapPin, Plus } from "lucide-react";

export default function RegisterRestaurantForm({
  onSuccess,
  onClose,
}: {
  onSuccess?: () => void;
  onClose: () => void;
}) {
  const [form, setForm] = useState({
    name: "",
    image: "",
    phone: "",
    email: "",
    openingHour: "08:00",
    closingHour: "22:00",
    description: "",
    address: {
      province: "",
      district: "",
      ward: "",
      detail: "",
      isDefault: true,
      latitude: 0,
      longitude: 0,
    },
  });
  const [loading, setLoading] = useState(false);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    if (name.startsWith("address.")) {
      setForm({
        ...form,
        address: { ...form.address, [name.replace("address.", "")]: value },
      });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  // Xử lý chọn địa điểm từ bản đồ
  const handleLocationSelect = async (latStr: string, lonStr: string) => {
    try {
      const lat = parseFloat(latStr);
      const lon = parseFloat(lonStr);
      const res = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&addressdetails=1`
      );
      const data = await res.json();
      const addr = data.address || {};

      // Tạo chi tiết địa chỉ từ house_number và road
      const detailParts: string[] = [];
      if (addr.house_number) detailParts.push(addr.house_number);
      if (addr.road) detailParts.push(addr.road);
      const detail = detailParts.join(" ");

      setForm((prev) => ({
        ...prev,
        address: {
          ...prev.address,
          province:
            (addr["ISO3166-2-lvl4"] === "VN-SG" ? "TP. Hồ Chí Minh" : "") ||
            addr.state ||
            "",
          district: addr.city || addr.town || addr.county || "",
          ward: addr.suburb || "",
          detail: addr.house_number
            ? `${addr.house_number} ${addr.road || ""}`
            : addr.road || "",
          latitude: lat,
          longitude: lon,
        },
      }));

      setIsDialogOpen(false);
      alert("Lấy thông tin địa chỉ thành công");
    } catch (err) {
      console.error(err);
      alert("Không thể lấy thông tin địa chỉ");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Kiểm tra xem đã chọn vị trí từ bản đồ chưa
    if (form.address.latitude === 0 || form.address.longitude === 0) {
      alert("Vui lòng chọn địa chỉ từ bản đồ trước khi đăng ký!");
      return;
    }

    setLoading(true);
    try {
      const res = await fetch("http://localhost:6969/grab/restaurants", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      if (res.ok) {
        alert("Đăng ký thành công! Vui lòng chờ admin duyệt.");
        onSuccess && onSuccess();
        onClose();
      } else {
        alert("Đăng ký thất bại!");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-3 p-2">
      <Label>Tên nhà hàng</Label>
      <Input name="name" value={form.name} onChange={handleChange} required />
      <Label>Ảnh đại diện (URL)</Label>
      <Input name="image" value={form.image} onChange={handleChange} />
      <Label>Số điện thoại</Label>
      <Input name="phone" value={form.phone} onChange={handleChange} required />
      <Label>Email</Label>
      <Input name="email" value={form.email} onChange={handleChange} required />
      <Label>Giờ mở cửa</Label>
      <Input
        name="openingHour"
        value={form.openingHour}
        onChange={handleChange}
        type="time"
        required
      />
      <Label>Giờ đóng cửa</Label>
      <Input
        name="closingHour"
        value={form.closingHour}
        onChange={handleChange}
        type="time"
        required
      />
      <Label>Mô tả</Label>
      <Textarea
        name="description"
        value={form.description}
        onChange={handleChange}
      />

      {/* Phần địa chỉ mới với LocationSearch */}
      <div className="space-y-2">
        <Label>Địa chỉ</Label>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button
              type="button"
              className="w-full flex items-center justify-center"
            >
              <MapPin className="h-4 w-4 mr-2" />
              {form.address.detail
                ? "Thay đổi địa chỉ"
                : "Chọn địa chỉ từ bản đồ"}
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Chọn địa chỉ trên bản đồ</DialogTitle>
            </DialogHeader>
            <LocationSearch onSelectLocation={handleLocationSelect} />
          </DialogContent>
        </Dialog>

        {form.address.latitude !== 0 && form.address.longitude !== 0 && (
          <div className="p-3 border rounded-md bg-gray-50 space-y-1">
            <p className="text-sm font-medium">Vị trí đã chọn:</p>
            <p className="text-sm">
              <span className="font-medium">Chi tiết:</span>{" "}
              {form.address.detail}
            </p>
            <p className="text-sm">
              <span className="font-medium">Phường/Xã:</span>{" "}
              {form.address.ward}
            </p>
            <p className="text-sm">
              <span className="font-medium">Quận/Huyện:</span>{" "}
              {form.address.district}
            </p>
            <p className="text-sm">
              <span className="font-medium">Tỉnh/Thành:</span>{" "}
              {form.address.province}
            </p>
            <p className="text-xs text-gray-500">
              <span className="font-medium">Tọa độ:</span>{" "}
              {form.address.latitude}, {form.address.longitude}
            </p>
          </div>
        )}

        {/* Giữ lại input để chỉnh sửa chi tiết địa chỉ */}
        <Input
          name="address.detail"
          placeholder="Chi tiết địa chỉ (số nhà, đường...)"
          value={form.address.detail}
          onChange={handleChange}
          required
        />
      </div>

      <div className="flex gap-2">
        <Button type="submit" disabled={loading}>
          Gửi đăng ký
        </Button>
        <Button type="button" variant="outline" onClick={onClose}>
          Hủy
        </Button>
      </div>
    </form>
  );
}
