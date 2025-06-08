import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { fetchWithAuth } from "@/utils/api";

const STATUS_LABELS: Record<string, string> = {
  ACTIVE: "Active",
  INACTIVE: "InActive",
  PENDING: "Pending",
  REJECTED: "Reject",
};

export default function RestaurantManagement() {
  const [restaurants, setRestaurants] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const fetchAll = async () => {
    setLoading(true);
    const res = await fetchWithAuth(
      "http://localhost:6969/grab/restaurants/all"
    );
    const data = await res.json();
    setRestaurants(data.data || []);
    setLoading(false);
  };

  useEffect(() => {
    fetchAll();
  }, []);
  const handleAction = async (id: number, action: string) => {
    let url = "";
    let method = "PUT";
    if (action === "approve") url = `/grab/restaurants/${id}/approve`;
    else if (action === "reject") url = `/grab/restaurants/${id}/reject`;
    else if (action === "inactive") url = `/grab/restaurants/${id}/inactive`;
    else if (action === "active") url = `/grab/restaurants/${id}/active`;
    else return;
    await fetchWithAuth(`http://localhost:6969${url}`, { method });
    fetchAll();
  };

  const getFilteredRestaurants = () => {
    return restaurants.filter((restaurant) => {
      const matchesSearch =
        restaurant.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        restaurant.phone.includes(searchTerm) ||
        restaurant.email.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesStatus =
        statusFilter === "ALL" ||
        restaurant.status?.toUpperCase() === statusFilter;

      return matchesSearch && matchesStatus;
    });
  };
  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Quản lý nhà hàng</h2>

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
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">InActive</option>
            <option value="PENDING">Pending</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>
        <Button onClick={fetchAll} variant="outline">
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
                <th className="text-left px-4 py-3 border-b">Tên</th>
                <th className="text-left px-4 py-3 border-b">SĐT</th>
                <th className="text-left px-4 py-3 border-b">Email</th>
                <th className="text-left px-4 py-3 border-b">Giờ mở cửa</th>
                <th className="text-left px-4 py-3 border-b">Trạng thái</th>
                <th className="text-left px-4 py-3 border-b">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {getFilteredRestaurants().length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-gray-500">
                    {searchTerm || statusFilter !== "ALL"
                      ? "Không tìm thấy nhà hàng nào"
                      : "Không có nhà hàng nào"}
                  </td>
                </tr>
              ) : (
                getFilteredRestaurants().map((r) => (
                  <tr key={r.id} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3">{r.name}</td>
                    <td className="px-4 py-3">{r.phone}</td>
                    <td className="px-4 py-3">{r.email}</td>
                    <td className="px-4 py-3">
                      {r.openingHour && r.closingHour
                        ? `${r.openingHour} - ${r.closingHour}`
                        : ""}
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`px-2 py-1 rounded-full text-xs font-medium ${
                          r.status === "ACTIVE"
                            ? "bg-green-100 text-green-800"
                            : r.status === "INACTIVE"
                            ? "bg-gray-100 text-gray-800"
                            : r.status === "PENDING"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {STATUS_LABELS[r.status?.toUpperCase() || "PENDING"] ||
                          r.status}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2 flex-wrap">
                        {r.status === "ACTIVE" && (
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => handleAction(r.id, "inactive")}
                            className="text-xs"
                          >
                            InActive
                          </Button>
                        )}
                        {r.status === "INACTIVE" && (
                          <Button
                            size="sm"
                            onClick={() => handleAction(r.id, "active")}
                            className="text-xs"
                          >
                            Active
                          </Button>
                        )}
                        {r.status === "PENDING" && (
                          <>
                            <Button
                              onClick={() => handleAction(r.id, "approve")}
                              size="sm"
                              className="text-xs mr-2"
                            >
                              Approve
                            </Button>
                            <Button
                              variant="destructive"
                              size="sm"
                              onClick={() => handleAction(r.id, "reject")}
                              className="text-xs"
                            >
                              Reject
                            </Button>
                          </>
                        )}
                        {r.status === "REJECTED" && (
                          <Button
                            onClick={() => handleAction(r.id, "approve")}
                            size="sm"
                            className="text-xs"
                          >
                            Approve
                          </Button>
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
    </div>
  );
}
