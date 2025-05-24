import { use, useEffect, useState } from "react";
import ModalAddVoucher from "./ModalAddVoucher";
import ModalAddVoucherDetail from "./ModalAddVoucherDetail";
import { fetchAdminVouchers, updateVoucher,deleteVoucher, createVoucher, addAdminVoucherDetail } from "@/utils/apiVoucher";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { AddAdminVoucherRequest, AddVoucherDetailRequest, Voucher } from "../types/voucher";
import DiscountDetailModal from "./ModalVoucherDetail";

export default function AdminVoucher() {
    const [modalAddOpen, setAddModalOpen] = useState(false);
    const [vouchers, setVouchers] = useState<Voucher[]>([]);
    const [selectedVoucher, setSelectedVoucher] = useState<Voucher | null>(null);
    const [modalAddDetailOpen, setAddDetailModalOpen] = useState(false);
    const [ModalDetailsOpen, setModalDetailsOpen] = useState(false);

  useEffect(() => {
    const load = async () => {
      const data = await fetchAdminVouchers();
      setVouchers(data);
    };
    load();
  }, [])

  const handleUpdate = async (voucherId: number) => {
      try {
        await updateVoucher(voucherId);
          fetchAdminVouchers().then((data) => {
          setVouchers(data);
        });
      } catch (error) {
        toast.error('Đã xảy ra lỗi khi cập nhật!'); 
      }
    };

  const handleDelete = async (voucherId: number) => {
    await deleteVoucher(voucherId);
    fetchAdminVouchers().then((data) => {
      setVouchers(data);
    });
  };

  const handleCreate = async (data: AddAdminVoucherRequest) => {
    await createVoucher(data);
    fetchAdminVouchers().then((data) => {
      setVouchers(data);
    });
    setAddModalOpen(false);
  }

  const handleAddDetail = async (data: AddVoucherDetailRequest) => {
    await addAdminVoucherDetail(data);
    fetchAdminVouchers().then((data) => {
      setVouchers(data);
    });
    setAddDetailModalOpen(false);
  }

    return (
    <div className="container mx-auto p-6 bg-gray-100 min-h-screen">
    {/* Header */}
    <div className="flex justify-between items-center mb-6">
      <h1 className="text-3xl font-extrabold text-gray-800">Quản lý Voucher</h1>
      <button
        className="bg-green-600 text-white px-5 py-2.5 rounded-lg hover:bg-green-700 transition shadow-md"
        onClick={() => setAddModalOpen(true)}
      >
        + Thêm Voucher
      </button>
    <ModalAddVoucher isOpen={modalAddOpen} onClose={() => setAddModalOpen(false)}
    onSubmit={handleCreate}
    />

    </div>

    {/* Voucher List */}
    <div className="bg-white shadow-lg rounded-xl overflow-hidden">
      <div className="max-h-[600px] overflow-y-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-green-600 text-white">
            <tr>
              <th className="py-4 px-6 text-left text-sm font-semibold">Code</th>
              <th className="py-4 px-6 text-left text-sm font-semibold">Giảm</th>
              <th className="py-4 px-6 text-left text-sm font-semibold">Loại</th>
              <th className="py-4 px-6 text-left text-sm font-semibold">Trạng thái</th>
              <th className="py-4 px-6 text-left text-sm font-semibold">Khóa</th>
              <th className="py-4 px-6 text-left text-sm font-semibold">Hành động</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {vouchers.map((voucher) => (
              <tr key={voucher.id} className="hover:bg-gray-50 transition">
                <td className="py-4 px-6 text-gray-700">{voucher.code}</td>
                <td className="py-4 px-6 text-gray-700">
                  {voucher.type === 'PERCENTAGE'
                    ? `${voucher.value.toLocaleString()}%`
                    : `${voucher.value.toLocaleString()}đ`}
                </td>
                
                <td className="py-4 px-6 text-gray-700">
                  {voucher.applyType === 'ORDER' ? 'ĐƠN' : 'SHIP'}
                </td>

                <td className="py-4 px-6">
                  <span
                    className={`inline-block px-2 py-1 rounded-full text-xs font-semibold ${
                      voucher.active ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'
                    }`}
                  >
                    {voucher.active ? 'Mở' : 'Đóng'}
                  </span>
                </td>
                <td className="py-4 px-6">
                  {voucher.status !== 'ACTIVE' ? (
                    <><button
                      className="bg-green-500 text-white px-3 py-1.5 rounded-lg hover:bg-green-600 transition shadow-sm"
                      onClick={() => handleUpdate(voucher.id)}
                    >
                      Mở
                    </button></>
                  ) : (

                      <button
                        className="bg-red-500 text-white px-3 py-1.5 rounded-lg hover:bg-red-600 transition shadow-sm"
                        onClick={() => handleUpdate(voucher.id)}
                      >
                        Khóa
                      </button>
                    )
                  }
                </td>
                <td className="py-4 px-6 flex gap-2">
                  <button
                    className="bg-red-500 text-white px-3 py-1.5 rounded-lg hover:bg-red-600 transition shadow-sm"
                    onClick={() => {
                      handleDelete(voucher.id);
                    }}
                  >
                    Xóa
                  </button>
                  <button
                    onClick={() => {
                      setSelectedVoucher(voucher);
                      setModalDetailsOpen(true);
                    }
                  }
                    className="bg-blue-500 text-white px-3 py-1.5 rounded-lg hover:bg-blue-600 transition shadow-sm"
                  >
                    Chi tiết
                  </button>
                  {selectedVoucher && (
                    <DiscountDetailModal
                      onOpen={ModalDetailsOpen}
                      onClose={() => setModalDetailsOpen(false)}
                      data={selectedVoucher}
                    />
                  )}
                  {voucher.status === 'ACTIVE' && !voucher.active && (
                    <><button
                      className="bg-green-500 text-white px-3 py-1.5 rounded-lg hover:bg-green-600 transition shadow-sm"
                      onClick={() => {
                        setAddDetailModalOpen(true);
                      } }
                    >
                      Tạo mới
                    </button><ModalAddVoucherDetail
                        voucherId={voucher.id}
                        isOpen={modalAddDetailOpen}
                        onClose={() => setAddDetailModalOpen(false)}
                        onSubmit={handleAddDetail}
                         /></>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
    <ToastContainer />
  </div>
  
);
}
