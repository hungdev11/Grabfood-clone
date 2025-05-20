'use client';

import { useState , useEffect} from 'react';
import { Voucher, VoucherRequest } from '@/components/types/voucher';
import { fetchVouchers, createVoucher, deleteVoucher, updateVoucher , addVoucherDetail} from '@/utils/apiVoucher';
import { useRouter } from 'next/navigation';
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import ModalVoucher from './modalVoucher';
import ModalAddDetail from './modalAddDetail';

import { useParams } from "next/navigation";

export default function VoucherManagement() {
  const params = useParams();
  const restaurantId = params?.restaurantId as string;
  const [showModal, setShowModal] = useState(false);
  const [showModalDetail, setShowModalDetail] = useState(false);
  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [selectedVoucher, setSelectedVoucher] = useState<Voucher | null>(null);

  const initialForm: VoucherRequest = {
  code: '',
  description: '',
  minRequire: 0,
  type: 'FIXED',
  value: 0,
  applyType: 'ORDER', // default
  status: 'ACTIVE',   // default
  restaurant_id: Number(restaurantId)   
};

  const [formData, setFormData] = useState(initialForm);


   const handleChange = (e: { target: { name: any; value: any; }; }) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'minRequire' || name === 'value' ? parseFloat(value) || '' : value,
    }));
  };

  useEffect(() => {
    const load = async () => {
      const data = await fetchVouchers(restaurantId);
      setVouchers(data);
    };
    load();
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createVoucher(formData);
      setTimeout(() => {
        setIsModalOpen(false);
      }, 1000);
      toast.success(`Thêm thành công!`);
      fetchVouchers(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (err) {
      console.error(err);
    } finally {
    }
  };

  const handleDelete = async (voucherId: number) => {
    try {
      await deleteVoucher(voucherId);
      fetchVouchers(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (error) {
      toast.error('Đã xảy ra lỗi khi xóa!');
    }
  }

  const handleUpdate = async (voucherId: number) => {
    try {
      await updateVoucher(voucherId);
      fetchVouchers(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (error) {
      toast.error('Đã xảy ra lỗi khi cập nhật!'); 
    }
  };

  const handleAddVoucherDetail = async (data: any) => {
    try {
      await addVoucherDetail(data);
      toast.success('Thêm chi tiết voucher thành công!');
      fetchVouchers(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (error) {
      toast.error('Đã xảy ra lỗi khi thêm chi tiết voucher!');
    }
  }

  const [isModalOpen, setIsModalOpen] = useState(false);


  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-green-500">Voucher Management</h1>
        <button
            onClick={() => setIsModalOpen(true)}
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
        >
            Thêm Voucher
        </button>
    </div>

      {/* Voucher List */}
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <table className="min-w-full">
          <thead className="bg-green-500 text-white">
            <tr>
              <th className="py-3 px-4 text-left">Code</th>
              <th className="py-3 px-4 text-left">Giảm</th>
              <th className="py-3 px-4 text-left">Trạng thái</th>
              <th className="py-3 px-4 text-left">Khóa</th>
              <th className="py-3 px-4 text-left">Action</th>
            </tr>
          </thead>
          <tbody>
            {vouchers.map((voucher) => (
              <tr key={voucher.id} className="border-b hover:bg-gray-50">
                <td className="py-3 px-4">{voucher.code}</td>
                <td className="py-3 px-4">
                  {voucher.type === 'PERCENTAGE' ? `${voucher.value.toLocaleString()}%` : `${voucher.value.toLocaleString()}đ`}
                </td>
                <td className="py-3 px-4">
                  {voucher.active ? (
                    <span className="text-green-500 font-semibold">Mở</span>)
                  : (
                    <span className="text-red-500 font-semibold">Đóng</span>
                  )}
                </td>
                <td className="py-3 px-4">
                  {voucher.status !== "ACTIVE" ? (
                    <button
                      className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
                      onClick={() => {handleUpdate(voucher.id)}}
                    >
                      Mở
                    </button>
                  ) : (
                    !voucher.active && (
                      <button
                        className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                        onClick={() => {handleUpdate(voucher.id)}}
                      >
                        Khóa
                      </button>
                    )
                  )}
                </td>
                <td className="py-3 px-4">
                  <button
                    className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                    onClick={() => handleDelete(voucher.id)}
                  >
                    Delete
                  </button>
                  <button
                    className="bg-pink-500 text-white px-3 py-1 rounded hover:bg-pink-600"
                    onClick={() => {
                      setShowModal(true);
                      setSelectedVoucher(voucher);
                    }}
                  >
                    Chi tiết
                  </button>
                  {voucher.status === 'ACTIVE' && !voucher.active && (
                    <button
                      className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
                      onClick={() => {
                        setShowModalDetail(true);
                        setSelectedVoucher(voucher);
                      }}
                    >
                      Tạo mới
                    </button>
                  )}
                  {showModalDetail && (
                    <ModalAddDetail
                      voucherId={selectedVoucher?.id}
                      isOpen={showModalDetail}
                      onClose={() => setShowModalDetail(false)}
                      onSubmit={handleAddVoucherDetail}
                    />
                  )}
                  {selectedVoucher && (
                    <ModalVoucher
                      isOpen={showModal}
                      onClose={() => setShowModal(false)}
                      voucher={selectedVoucher}
                    />
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-lg w-full max-w-md">
        <h2 className="text-xl font-bold text-green-500 mb-4"> New AddVoucher</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Voucher Code</label>
            <input
              type="text"
              name="code"
              value={formData.code}
              onChange={handleChange}
              placeholder="e.g., SUMMER25K"
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Description</label>
            <input
              type="text"
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="e.g., Giảm 25K cho đơn mùa hè"
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Minimum Order Value (VND)</label>
            <input
              type="number"
              name="minRequire"
              value={formData.minRequire}
              onChange={handleChange}
              placeholder="e.g., 200000"
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              min="0"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Discount Type</label>
            <select
              name="type"
              value={formData.type}
              onChange={handleChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            >
              <option value="FIXED">Fixed Amount (VND)</option>
              <option value="PERCENTAGE">Percentage (%)</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Discount Value ({formData.type === 'FIXED' ? 'VND' : '%'})
            </label>
            <input
              type="number"
              name="value"
              value={formData.value}
              onChange={handleChange}
              placeholder={formData.type === 'FIXED' ? 'e.g., 25000' : 'e.g., 25'}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              min="0"
              required
            />
          </div>
          <div className="mt-6 flex justify-end space-x-2">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="bg-gray-300 text-black px-4 py-2 rounded hover:bg-gray-400"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
            >
              Add
            </button>
          </div>
        </form>
      </div>
    </div>
      )}
      <ToastContainer />
    </div>
  );
}