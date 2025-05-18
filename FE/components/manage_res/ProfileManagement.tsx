"use client";
import React, { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import axios from "axios";
import { Restaurant } from "../types/Types";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
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
    const [originalImage, setOriginalImage] = useState<string | undefined>(undefined);
    const [verifyingAddress, setVerifyingAddress] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState(false);

    if (!restaurantId) {
        return <div>Restaurant ID not found</div>;
    }

    useEffect(() => {
        const fetchRestaurant = async () => {
            try {
                const res = await axios.get(`http://localhost:6969/grab/restaurants/${restaurantId}?userLat=-1&userLon=-1`);
                setProfile(res.data.data);
                setEditData(res.data.data); // preload edit data
                setOriginalImage(res.data.data.image); // lưu ảnh gốc
            } catch (error) {
                console.error("Lỗi khi lấy thông tin nhà hàng:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchRestaurant();
    }, [restaurantId]);

    const getChangedFields = (original: any, edited: any) => {
        const changed: any = {};
        Object.keys(edited).forEach((key) => {
            const newValue = edited[key];

            // Nếu giá trị là chuỗi rỗng hoặc null, không gửi lên
            if (
                newValue !== undefined &&
                newValue !== null &&
                !(typeof newValue === "string" && newValue.trim() === "") &&
                newValue !== original[key]
            ) {
                changed[key] = newValue;
            }
        });
        return changed;
    };

    const handleVerifyAddress = async () => {
        if (!editData.address || editData.address.trim() === "") {
            alert("Vui lòng nhập địa chỉ để xác thực.");
            return;
        }

        try {
            setVerifyingAddress(true);
            // Gọi Nominatim geocoding
            const res = await fetch(
            `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(editData.address)}&addressdetails=1&limit=1`
            );
            const data = await res.json();

            if (data.length === 0) {
                alert("Không tìm thấy tọa độ cho địa chỉ này.");
                return;
            }

            const place = data[0];

            setEditData(prev => ({
                ...prev,
                latitude: parseFloat(place.lat),
                longitude: parseFloat(place.lon),
                address: place.display_name, // bạn có thể giữ nguyên hoặc thay đổi
            }));

            alert("Xác thực địa chỉ thành công!");
        } catch (error) {
            console.error("Lỗi xác thực địa chỉ:", error);
            alert("Xác thực địa chỉ thất bại.");
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
        console.log("Địa chỉ:", addr);
        const detail = [addr.house_number, addr.road].filter(Boolean).join(" ");

        const updatedLocation = {
            latitude: lat,
            longitude: lon,
            address: data.display_name,
            province: addr.state || "",
            district: addr.city || addr.town || addr.county || "",
            ward: addr.suburb || "",
            detail,
        };
        setEditData((prev) => ({
        ...prev,
        ...updatedLocation,
        }));

        alert("Lấy thông tin địa chỉ thành công");
    } catch (err) {
        console.error(err);
        alert("Không thể lấy thông tin địa chỉ");
    }
    };

    const handleSave = async () => {
        if (!profile) return;
        const changes = getChangedFields(profile, editData);
        console.log("Thay đổi:", changes);
        if (Object.keys(changes).length === 0) {
            alert("Không có thay đổi nào.");
            setIsEditing(false);
            return;
        }

        try {
            await axios.put(`http://localhost:6969/grab/restaurants/${restaurantId}`, changes);
            alert("Cập nhật thành công!");
            setProfile({ ...profile, ...changes });
            setIsEditing(false);
        } catch (error) {
            console.error("Lỗi khi cập nhật:", error);
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
                    className="w-40 h-40 object-cover rounded-lg border" />
                <div className="flex-1 space-y-2">
                {isEditing ? (
                    <>
                        <div>
                            <label className="block font-semibold mb-1">Tên:</label>
                            <input className="border p-2 w-full" value={editData.name || ""} onChange={(e) => setEditData({ ...editData, name: e.target.value })} />
                        </div>
                        <div>
                            <label className="block font-semibold mb-1">Ảnh:</label>
                            <input className="border p-2 w-full" value={editData.image || ""} onChange={(e) => setEditData({ ...editData, image: e.target.value })} />
                        </div>
                        <div>
                            <label className="block font-semibold mb-1">Số Điện thoại:</label>
                            <input className="border p-2 w-full" value={editData.phone || ""} onChange={(e) => setEditData({ ...editData, phone: e.target.value })} />
                        </div>
                        <div>
                            <label className="block font-semibold mb-1">Giờ mở cửa:</label>
                            <input className="border p-2 w-full" value={editData.openingHour || ""} onChange={(e) => setEditData({ ...editData, openingHour: e.target.value })} />
                            <span>
                                <label className="block font-semibold mb-1">Giờ đóng cửa:</label>
                                <input className="border p-2 w-full" value={editData.closingHour || ""} onChange={(e) => setEditData({ ...editData, closingHour: e.target.value })} />
                            </span>
                        </div>
                        {/* <div>
                            <label className="block font-semibold mb-1">Địa chỉ:</label>
                            <input className="border p-2 w-full" value={editData.address || ""} onChange={(e) => setEditData({ ...editData, address: e.target.value })} />
                        </div> */}
                        <div>
                            <Label className="block font-semibold mb-1">Địa chỉ</Label>
                            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                                <DialogTrigger asChild>
                                    <Button type="button" className="mb-2" onClick={() => setIsDialogOpen(true)}>
                                    <Plus className="h-4 w-4 mr-2" />
                                    Chọn địa chỉ từ bản đồ
                                    </Button>
                                </DialogTrigger>
                                <DialogContent>
                                    <DialogHeader>
                                    <DialogTitle>Chọn địa chỉ trên bản đồ</DialogTitle>
                                    </DialogHeader>
                                    <div className="pt-4 space-y-4">
                                    <LocationSearch onSelectLocation={handleLocationSelect} />

                                    <div className="flex justify-end gap-2">
                                        <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                                        Hủy
                                        </Button>
                                        <Button onClick={() => setIsDialogOpen(false)}>
                                        Xong
                                        </Button>
                                    </div>
                                    </div>
                                </DialogContent>
                                </Dialog>

                            {/* Chi tiết địa chỉ */}
                            <div className="flex gap-2">
                                 <Input
                                    placeholder="Ví dụ: 123 Nguyễn Huệ"
                                    className="mt-2"
                                    value={editData.address || ""}
                                    onChange={(e) => setEditData({ ...editData, address: e.target.value })}
                            />
                            </div>

                            {/* Nút xác thực địa chỉ */}
                            <div className="flex gap-2 mt-2">
                                <Button
                                onClick={handleVerifyAddress}
                                disabled={verifyingAddress}
                                className="mb-2"
                                >
                                    {verifyingAddress ? "Đang xác thực..." : "Xác nhận địa chỉ"}
                                </Button>
                            </div>

                            <div className="text-sm text-gray-500 mt-2">
                                <p><strong>Latitude:</strong> {editData.latitude || "N/A"}</p>
                                <p><strong>Longitude:</strong> {editData.longitude || "N/A"}</p>
                            </div>
                            </div>
                        <div>
                            <label className="block font-semibold mb-1">Mô tả:</label>
                            <textarea className="border p-2 w-full" value={editData.description || ""} onChange={(e) => setEditData({ ...editData, description: e.target.value })} />
                        </div>
                        <div className="space-x-2 mt-2">
                            <button onClick={handleSave} className="px-4 py-2 bg-green-500 text-white rounded">Lưu</button>
                            <button onClick={() => { 
                                setEditData(profile); 
                                setIsEditing(false); 
                                setEditData({ ...profile, image: originalImage });
                            }} className="px-4 py-2 bg-gray-500 text-white rounded">Hủy</button>
                        </div>
                    </>
                ) : (

                        <>
                            <p><strong>Tên:</strong> {profile.name}</p>
                            <p><strong>Điện thoại:</strong> {profile.phone}</p>
                            <p><strong>Giờ hoạt động:</strong> {profile.openingHour} - {profile.closingHour}</p>
                            <p><strong>Địa chỉ:</strong> {profile.address}</p>
                            <p><strong>Đánh giá:</strong> {profile.rating} ⭐</p>
                            <p><strong>Mô tả:</strong> {profile.description}</p>
                            <button onClick={() => setIsEditing(true)} className="px-4 py-2 bg-blue-500 text-white rounded">Cập nhật thông tin</button>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
