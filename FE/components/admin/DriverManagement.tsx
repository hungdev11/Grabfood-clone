import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { fetchWithAuth } from "@/utils/api";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { toast } from "@/components/ui/use-toast";

const STATUS_LABELS: Record<string, string> = {
  ACTIVE: "Hoạt động",
  INACTIVE: "Không hoạt động",
  SUSPENDED: "Tạm khóa",
};

const STATUS_COLORS: Record<string, string> = {
  ACTIVE: "text-green-600",
  INACTIVE: "text-gray-600",
  SUSPENDED: "text-red-600",
};

interface Driver {
  id: number;
  name: string;
  phone: string;
  email: string;
  status: string;
  vehicleType?: string;
  licensePlate?: string;
}

export default function DriverManagement() {
  const [drivers, setDrivers] = useState<Driver[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [confirmDialog, setConfirmDialog] = useState<{
    open: boolean;
    driverId: number;
    newStatus: string;
    actionLabel: string;
    driverName: string;
  }>({
    open: false,
    driverId: 0,
    newStatus: "",
    actionLabel: "",
    driverName: "",
  });

  const fetchAllDrivers = async () => {
    setLoading(true);
    try {
      const res = await fetchWithAuth(
        "http://localhost:6969/grab/admin/drivers"
      );
      const data = await res.json();
      setDrivers(data.data || []);
    } catch (error) {
      console.error("Error fetching drivers:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllDrivers();
  }, []);
  const handleStatusChange = async (driverId: number, newStatus: string) => {
    try {
      const url = `http://localhost:6969/grab/admin/drivers/status/${driverId}?newStatus=${newStatus}`;
      await fetchWithAuth(url, {
        method: "PUT",
      });

      // Refresh the drivers list after successful status change
      await fetchAllDrivers();

      // Close dialog and show success message
      setConfirmDialog({ ...confirmDialog, open: false });
      toast({
        title: "Thành công",
        description: `Đã cập nhật trạng thái tài xế thành ${STATUS_LABELS[newStatus]}`,
      });
    } catch (error) {
      console.error("Error updating driver status:", error);
      toast({
        title: "Lỗi",
        description: "Có lỗi xảy ra khi cập nhật trạng thái tài xế",
        variant: "destructive",
      });
    }
  };

  const openConfirmDialog = (
    driverId: number,
    newStatus: string,
    actionLabel: string,
    driverName: string
  ) => {
    setConfirmDialog({
      open: true,
      driverId,
      newStatus,
      actionLabel,
      driverName,
    });
  };
  const getAvailableActions = (currentStatus: string) => {
    switch (currentStatus) {
      case "ACTIVE":
        return [
          { action: "INACTIVE", label: "Tạm ngừng", variant: "secondary" },
          {
            action: "SUSPENDED",
            label: "Khóa tài khoản",
            variant: "destructive",
          },
        ];
      case "INACTIVE":
        return [
          { action: "ACTIVE", label: "Kích hoạt", variant: "default" },
          {
            action: "SUSPENDED",
            label: "Khóa tài khoản",
            variant: "destructive",
          },
        ];
      case "SUSPENDED":
        return [
          { action: "ACTIVE", label: "Kích hoạt", variant: "default" },
          { action: "INACTIVE", label: "Tạm ngừng", variant: "secondary" },
        ];
      default:
        return [];
    }
  };

  const getFilteredDrivers = () => {
    return drivers.filter((driver) => {
      const matchesSearch =
        driver.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        driver.phone.includes(searchTerm) ||
        driver.email.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesStatus =
        statusFilter === "ALL" || driver.status?.toUpperCase() === statusFilter;

      return matchesSearch && matchesStatus;
    });
  };
  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Quản lý tài xế</h2>

      {/* Search and Filter Section */}
      <div className="mb-4 flex gap-4 items-center">
        <div className="flex-1">
          <input
            type="text"
            placeholder="Tìm kiếm theo tên, số điện thoại hoặc email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
          />
        </div>
        <div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="ACTIVE">Hoạt động</option>
            <option value="INACTIVE">Không hoạt động</option>
            <option value="SUSPENDED">Tạm khóa</option>
          </select>
        </div>
        <Button onClick={fetchAllDrivers} variant="outline">
          Làm mới
        </Button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-8">
          <div className="text-gray-500">Đang tải...</div>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full border border-collapse bg-white rounded-lg shadow">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-4 py-3 border-b">ID</th>
                <th className="text-left px-4 py-3 border-b">Họ tên</th>
                <th className="text-left px-4 py-3 border-b">SĐT</th>
                <th className="text-left px-4 py-3 border-b">Email</th>
                <th className="text-left px-4 py-3 border-b">Loại xe</th>
                <th className="text-left px-4 py-3 border-b">Biển số</th>{" "}
                <th className="text-left px-4 py-3 border-b">Trạng thái</th>
                <th className="text-left px-4 py-3 border-b">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {getFilteredDrivers().length === 0 ? (
                <tr>
                  <td colSpan={8} className="text-center py-8 text-gray-500">
                    {searchTerm || statusFilter !== "ALL"
                      ? "Không tìm thấy tài xế nào"
                      : "Không có tài xế nào"}
                  </td>
                </tr>
              ) : (
                getFilteredDrivers().map((driver) => (
                  <tr key={driver.id} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3">{driver.id}</td>
                    <td className="px-4 py-3">{driver.name}</td>
                    <td className="px-4 py-3">{driver.phone}</td>
                    <td className="px-4 py-3">{driver.email}</td>
                    <td className="px-4 py-3">{driver.vehicleType || "-"}</td>
                    <td className="px-4 py-3">{driver.licensePlate || "-"}</td>
                    <td className="px-4 py-3">
                      <span
                        className={`font-medium ${
                          STATUS_COLORS[driver.status?.toUpperCase()] ||
                          "text-gray-600"
                        }`}
                      >
                        {" "}
                        {STATUS_LABELS[driver.status?.toUpperCase()] ||
                          driver.status}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2 flex-wrap">
                        {getAvailableActions(driver.status?.toUpperCase()).map(
                          (actionItem) => (
                            <Button
                              key={actionItem.action}
                              variant={actionItem.variant as any}
                              size="sm"
                              onClick={() =>
                                openConfirmDialog(
                                  driver.id,
                                  actionItem.action,
                                  actionItem.label,
                                  `${driver.name}`
                                )
                              }
                              className="text-xs"
                            >
                              {actionItem.label}
                            </Button>
                          )
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Confirmation Dialog */}
      <Dialog
        open={confirmDialog.open}
        onOpenChange={(open) => setConfirmDialog({ ...confirmDialog, open })}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Xác nhận thay đổi trạng thái</DialogTitle>
          </DialogHeader>
          <p>
            Bạn có chắc chắn muốn{" "}
            <strong>{confirmDialog.actionLabel.toLowerCase()}</strong> tài xế{" "}
            <strong>{confirmDialog.driverName}</strong>?
          </p>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() =>
                setConfirmDialog({ ...confirmDialog, open: false })
              }
            >
              Hủy
            </Button>
            <Button
              onClick={() =>
                handleStatusChange(
                  confirmDialog.driverId,
                  confirmDialog.newStatus
                )
              }
            >
              Xác nhận
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
