// components/RegisterRestaurantForm.tsx
import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
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
      <Label>Địa chỉ</Label>
      <Input
        name="address.province"
        placeholder="Tỉnh/Thành"
        value={form.address.province}
        onChange={handleChange}
        required
      />
      <Input
        name="address.district"
        placeholder="Quận/Huyện"
        value={form.address.district}
        onChange={handleChange}
        required
      />
      <Input
        name="address.ward"
        placeholder="Phường/Xã"
        value={form.address.ward}
        onChange={handleChange}
        required
      />
      <Input
        name="address.detail"
        placeholder="Số nhà, đường..."
        value={form.address.detail}
        onChange={handleChange}
        required
      />
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
