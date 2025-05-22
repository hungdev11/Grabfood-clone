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

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Quản lý nhà hàng</h2>
      {loading ? (
        <div>Đang tải...</div>
      ) : (
        <table className="w-full border border-collapse">
          <thead>
            <tr>
              <th className="text-left px-4 py-2">Tên</th>
              <th className="text-left px-4 py-2">SĐT</th>
              <th className="text-left px-4 py-2">Email</th>
              <th className="text-left px-4 py-2">Giờ mở cửa</th>
              <th className="text-left px-4 py-2">Trạng thái</th>
              <th className="text-left px-4 py-2">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {restaurants.map((r) => (
              <tr key={r.id} className="border-t">
                <td className="px-4 py-2">{r.name}</td>
                <td className="px-4 py-2">{r.phone}</td>
                <td className="px-4 py-2">{r.email}</td>
                <td className="px-4 py-2">
                  {r.openingHour && r.closingHour
                    ? `${r.openingHour} - ${r.closingHour}`
                    : ""}
                </td>
                <td className="px-4 py-2">
                  {STATUS_LABELS[r.status?.toUpperCase() || "PENDING"] ||
                    r.status}
                </td>
                <td className="px-4 py-2 space-x-2">
                  {r.status === "ACTIVE" && (
                    <Button
                      variant="destructive"
                      onClick={() => handleAction(r.id, "inactive")}
                    >
                      InActive
                    </Button>
                  )}
                  {r.status === "INACTIVE" && (
                    <Button onClick={() => handleAction(r.id, "active")}>
                      Active
                    </Button>
                  )}
                  {r.status === "PENDING" && (
                    <>
                      <Button
                        onClick={() => handleAction(r.id, "approve")}
                        className="mr-2"
                      >
                        Approve
                      </Button>
                      <Button
                        variant="destructive"
                        onClick={() => handleAction(r.id, "reject")}
                      >
                        Reject
                      </Button>
                    </>
                  )}
                  {r.status === "REJECTED" && (
                    <Button onClick={() => handleAction(r.id, "approve")}>
                      Approve
                    </Button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
