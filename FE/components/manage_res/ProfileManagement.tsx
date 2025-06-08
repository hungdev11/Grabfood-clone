"use client";
import React, { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import axios from "axios";
import axiosInstance from "@/utils/axiosInstance";
import { toast, Toaster } from "react-hot-toast";
import { Restaurant, UpdateRestaurant, AddressRequest } from "../types/Types";
import { logout } from "@/utils/authService";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/components/ui/dialog";
import LocationSearch from "../locationSearch";
import { Label } from "../ui/label";
import { Button } from "../ui/button";
import { Plus } from "lucide-react";
import { Input } from "../ui/input";

export default function ProfileManagement() {
  const { restaurantId } = useParams();
  const [profile, setProfile] = useState<Restaurant | null>(null);
  const [editData, setEditData] = useState<Partial<Restaurant>>({});
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [verifyingAddress, setVerifyingAddress] = useState(false);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [passwordDialog, setPasswordDialog] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  // Thêm hàm xử lý thay đổi mật khẩu
  const handlePasswordChange = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      toast.error("Mật khẩu mới không khớp!");
      return;
    }

    try {
      const response = await axiosInstance.put(
        "/grab/auth/user/change-password",
        {
          currentPassword: passwordData.currentPassword,
          newPassword: passwordData.newPassword,
          confirmPassword: passwordData.confirmPassword,
        }
      );

      if (response.status === 200) {
        toast.success("Đổi mật khẩu thành công!");
        setPasswordDialog(false);
        setPasswordData({
          currentPassword: "",
          newPassword: "",
          confirmPassword: "",
        });
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || "Đổi mật khẩu thất bại!");
    }
  };
  // Địa chỉ chi tiết để phục vụ cho UpdateRestaurant (không lưu vào Restaurant)
  const [addressDetail, setAddressDetail] = useState<Partial<AddressRequest>>(
    {}
  );

  useEffect(() => {
    const fetchRestaurant = async () => {
      try {
        const res = await axios.get(
          `http://localhost:6969/grab/restaurants/${restaurantId}?userLat=-1&userLon=-1`
        );
        setProfile(res.data.data);
        setEditData(res.data.data);
      } catch (error) {
        console.error("Lỗi khi lấy thông tin nhà hàng:", error);
      } finally {
        setLoading(false);
      }
    };
    if (restaurantId) fetchRestaurant();
  }, [restaurantId]);

  const getChangedFields = () => {
    if (!profile) return {};
    const changes: Partial<UpdateRestaurant> = {};

    if (editData.name && editData.name !== profile.name)
      changes.name = editData.name;
    if (editData.description && editData.description !== profile.description)
      changes.description = editData.description;
    if (editData.phone && editData.phone !== profile.phone)
      changes.phone = editData.phone;
    if (editData.openingHour && editData.openingHour !== profile.openingHour)
      changes.openingHour = editData.openingHour;
    if (editData.closingHour && editData.closingHour !== profile.closingHour)
      changes.closingHour = editData.closingHour;
    if (
      editData.latitude !== profile.latitude ||
      editData.longitude !== profile.longitude ||
      editData.address !== profile.address
    ) {
      if (
        addressDetail.latitude ||
        addressDetail.longitude ||
        addressDetail.detail ||
        addressDetail.ward ||
        addressDetail.district ||
        addressDetail.province
      ) {
        changes.address = addressDetail as AddressRequest;
      }
    }

    return changes;
  };

  const handleVerifyAddress = async () => {
    if (!editData.address?.trim()) {
      alert("Vui lòng nhập địa chỉ.");
      return;
    }

    try {
      setVerifyingAddress(true);
      const res = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(
          editData.address
        )}&addressdetails=1&limit=1`
      );
      const data = await res.json();
      if (data.length === 0) {
        alert("Không tìm thấy tọa độ.");
        return;
      }

      const place = data[0];
      const addr = place.address || {};

      setEditData((prev) => ({
        ...prev,
        latitude: parseFloat(place.lat),
        longitude: parseFloat(place.lon),
        address: place.display_name,
      }));

      setAddressDetail({
        latitude: place.lat,
        longitude: place.lon,
        detail: addr.house_number
          ? `${addr.house_number} ${addr.road || ""}`
          : addr.road || "",
        province:
          (addr["ISO3166-2-lvl4"] === "VN-SG" ? "TP. Hồ Chí Minh" : "") || "",
        district: addr.city || "", // Partido de Malvinas Argentinas
        ward: addr.suburb || "", // El Triángulo
      });

      alert("Xác thực thành công!");
    } catch (error) {
      console.error("Lỗi xác thực:", error);
      alert("Xác thực thất bại.");
    } finally {
      setVerifyingAddress(false);
    }
  };

  const handleLocationSelect = async (latStr: string, lonStr: string) => {
    try {
      const lat = parseFloat(latStr);
      const lon = parseFloat(lonStr);
      const res = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&addressdetails=1`
      );
      const data = await res.json();
      const addr = data.address || {};
      const detail = [addr.house_number, addr.road].filter(Boolean).join(" ");

      setEditData((prev) => ({
        ...prev,
        latitude: lat,
        longitude: lon,
        address: data.display_name,
      }));

      setAddressDetail({
        latitude: lat.toString(),
        longitude: lon.toString(),
        detail: addr.house_number
          ? `${addr.house_number} ${addr.road || ""}`
          : addr.road || "",
        province:
          (addr["ISO3166-2-lvl4"] === "VN-SG" ? "TP. Hồ Chí Minh" : "") || "",
        district: addr.city || "", // Partido de Malvinas Argentinas
        ward: addr.suburb || "", // El Triángulo
      });

      alert("Lấy địa chỉ thành công");
    } catch (err) {
      console.error(err);
      alert("Không thể lấy thông tin địa chỉ");
    }
  };

  const handleSave = async () => {
    const changes = getChangedFields();
    console.log("Thay đổi:", changes);
    if (Object.keys(changes).length === 0) {
      alert("Không có thay đổi.");
      setIsEditing(false);
      return;
    }

    try {
      await axiosInstance.put(`/grab/restaurants/${restaurantId}`, changes);
      alert("Cập nhật thành công!");
      setProfile((prev) => (prev ? { ...prev, ...editData } : null));
      setIsEditing(false);
    } catch (error) {
      console.error("Lỗi cập nhật:", error);
      alert("Cập nhật thất bại.");
    }
  };

  if (loading) return <p>Đang tải thông tin nhà hàng...</p>;
  if (!profile) return <p>Không tìm thấy thông tin nhà hàng.</p>;

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Thông tin Nhà Hàng</h2>
      <div className="flex items-start gap-6">
        <img
          src={editData.image || profile.image}
          alt={profile.name}
          className="w-40 h-40 object-cover rounded-lg border"
        />
        <div className="flex-1 space-y-2">
          {isEditing ? (
            <>
              <Input
                className="border p-2 w-full"
                value={editData.name || ""}
                placeholder="Tên nhà hàng"
                onChange={(e) =>
                  setEditData({ ...editData, name: e.target.value })
                }
              />
              <Input
                className="border p-2 w-full"
                value={editData.email || ""}
                placeholder="Email"
                disabled
              />
              <Input
                className="border p-2 w-full"
                value={editData.image || ""}
                placeholder="Link ảnh"
                onChange={(e) =>
                  setEditData({ ...editData, image: e.target.value })
                }
              />
              <Input
                className="border p-2 w-full"
                value={editData.phone || ""}
                placeholder="Số điện thoại"
                onChange={(e) =>
                  setEditData({ ...editData, phone: e.target.value })
                }
              />
              <Input
                className="border p-2 w-full"
                value={editData.openingHour || ""}
                placeholder="Giờ mở cửa"
                onChange={(e) =>
                  setEditData({ ...editData, openingHour: e.target.value })
                }
              />
              <Input
                className="border p-2 w-full"
                value={editData.closingHour || ""}
                placeholder="Giờ đóng cửa"
                onChange={(e) =>
                  setEditData({ ...editData, closingHour: e.target.value })
                }
              />

              <Label className="block font-semibold mt-4">Địa chỉ</Label>
              <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogTrigger asChild>
                  <Button onClick={() => setIsDialogOpen(true)}>
                    <Plus className="h-4 w-4 mr-2" />
                    Chọn địa chỉ từ bản đồ
                  </Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle>Chọn địa chỉ trên bản đồ</DialogTitle>
                  </DialogHeader>
                  <LocationSearch onSelectLocation={handleLocationSelect} />
                </DialogContent>
              </Dialog>

              <Input
                className="mt-2"
                value={editData.address || ""}
                placeholder="Nhập địa chỉ"
                onChange={(e) =>
                  setEditData({ ...editData, address: e.target.value })
                }
              />
              <Button onClick={handleVerifyAddress} disabled={verifyingAddress}>
                {verifyingAddress ? "Đang xác thực..." : "Xác nhận địa chỉ"}
              </Button>

              <div className="text-sm text-gray-500 mt-2">
                <p>
                  <strong>Latitude:</strong> {editData.latitude || "N/A"}
                </p>
                <p>
                  <strong>Longitude:</strong> {editData.longitude || "N/A"}
                </p>
              </div>

              <textarea
                className="border p-2 w-full"
                value={editData.description || ""}
                placeholder="Mô tả"
                onChange={(e) =>
                  setEditData({ ...editData, description: e.target.value })
                }
              />

              <div className="space-x-2 mt-4">
                <Button onClick={handleSave} className="bg-green-500">
                  Lưu
                </Button>
                <Button
                  variant="outline"
                  onClick={() => {
                    setEditData(profile);
                    setIsEditing(false);
                    setAddressDetail({});
                  }}
                >
                  Hủy
                </Button>
              </div>
            </>
          ) : (
            <>
              <p>
                <strong>Tên:</strong> {profile.name}
              </p>
              <p>
                <strong>Email:</strong> {profile.email || "N/A"}
              </p>
              <p>
                <strong>Mật khẩu:</strong> ********{" "}
                <Button
                  onClick={() => setPasswordDialog(true)}
                  className="text-blue-500 text-sm underline"
                  variant="link"
                >
                  Thay đổi
                </Button>
              </p>
              <p>
                <strong>Điện thoại:</strong> {profile.phone}
              </p>
              <p>
                <strong>Giờ hoạt động:</strong> {profile.openingHour} -{" "}
                {profile.closingHour}
              </p>
              <p>
                <strong>Địa chỉ:</strong> {profile.address}
              </p>
              <p>
                <strong>Đánh giá:</strong> {profile.rating} ⭐
              </p>
              <p>
                <strong>Mô tả:</strong> {profile.description}
              </p>
              <div className="flex space-x-2 mt-4">
                <Button
                  onClick={() => setIsEditing(true)}
                  className="bg-blue-500 text-white"
                >
                  Cập nhật thông tin
                </Button>
                <Button
                  onClick={() => {
                    if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
                      logout();
                    }
                  }}
                  className="bg-red-500 text-white"
                >
                  Đăng xuất
                </Button>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Password Change Dialog */}
      <Dialog open={passwordDialog} onOpenChange={setPasswordDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Thay đổi mật khẩu</DialogTitle>
          </DialogHeader>
          <form onSubmit={handlePasswordChange}>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="currentPassword">Mật khẩu hiện tại</Label>
                <Input
                  id="currentPassword"
                  type="password"
                  value={passwordData.currentPassword}
                  onChange={(e) =>
                    setPasswordData({
                      ...passwordData,
                      currentPassword: e.target.value,
                    })
                  }
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="newPassword">Mật khẩu mới</Label>
                <Input
                  id="newPassword"
                  type="password"
                  value={passwordData.newPassword}
                  onChange={(e) =>
                    setPasswordData({
                      ...passwordData,
                      newPassword: e.target.value,
                    })
                  }
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Xác nhận mật khẩu mới</Label>
                <Input
                  id="confirmPassword"
                  type="password"
                  value={passwordData.confirmPassword}
                  onChange={(e) =>
                    setPasswordData({
                      ...passwordData,
                      confirmPassword: e.target.value,
                    })
                  }
                  required
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setPasswordDialog(false)}
              >
                Hủy
              </Button>
              <Button type="submit">Lưu thay đổi</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
      <Toaster position="top-center" />
    </div>
  );
}
