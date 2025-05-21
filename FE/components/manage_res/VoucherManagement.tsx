'use client';

import { useState , useEffect} from 'react';
import { Voucher, VoucherRequest } from '@/components/types/voucher';
import { fetchVouchersRestaurant, createVoucher, deleteVoucherRes, updateVoucher , addVoucherDetailRes, createVoucherRestaurant} from '@/utils/apiVoucher';
import { useRouter } from 'next/navigation';
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import ModalVoucher from './modalVoucher';
import ModalAddDetail from './modalAddDetail';

import { useParams } from "next/navigation";
import { Food } from '../types/Types';
import ModalDetails from './modelDetails';
import { get } from 'http';

export default function VoucherManagement() {
  const params = useParams();
  const restaurantId = params?.restaurantId as string;
  const [showModal, setShowModal] = useState(false);
  const [showModalDetail, setShowModalDetail] = useState(false);
  const [showModalVoucher, setShowModalVoucher] = useState(false); // Chi tiết voucher
  const [showModalDetails, setShowModalDetails] = useState(false); // Chi tiết món

  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [selectedVoucher, setSelectedVoucher] = useState<Voucher | null>(null);
  const [foods, setFoods] = useState<Food[]>([]);
  const initialForm: VoucherRequest = {
    code: '',
    description: '',
    type: 'FIXED',
    value: 0,
    applyType: 'ALL',
    status: 'ACTIVE',
    restaurant_id: Number(restaurantId),
    foodIds: [],
    startDate: '',
    endDate: '',
  };
  const formatDateTime = (dateTime: string): string => {
    if (!dateTime) return '';
    // Chuyển đổi thành định dạng YYYY-MM-DDTHH:mm:ss
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = '00'; // Thêm giây mặc định là 00
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  };
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const filteredFoods = foods.filter(food =>
    food.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const [formData, setFormData] = useState(initialForm);

  const getFoods = async () => {
    try {
      const response = await fetch(`http://localhost:6969/grab/foods/all/restaurant/${restaurantId}`);
      const data = await response.json();
      setFoods(data.data);
    } catch (error) {
      console.error('Error fetching foods:', error);
    }
  };

  useEffect(() => {
    if (formData.applyType === 'SPECIFIC') {
      getFoods();
    }
  }, [formData.applyType]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;

    const newValue = name === 'value' ? Number(value) || 0 : value;

    setFormData((prev) => ({
      ...prev,
      [name]: newValue,
    }));

    console.log("Field changed:", name, "→", newValue);
  };

  useEffect(() => {
    const load = async () => {
      const data = await fetchVouchersRestaurant(restaurantId);
      setVouchers(data);
    };
    load();
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const formattedData = {
        ...formData,
        startDate: formatDateTime(formData.startDate),
        endDate: formatDateTime(formData.endDate),
      };
      console.log("Formatted data:", formattedData);
      await createVoucherRestaurant(formattedData);
      setTimeout(() => {
        setIsModalOpen(false);
      }, 1000);
      toast.success(`Thêm thành công!`);
      fetchVouchersRestaurant(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (err) {
      console.error(err);
    } finally {
    }
  };

  const handleDelete = async (voucherId: number) => {
    try {
      await deleteVoucherRes(restaurantId, voucherId);
      fetchVouchersRestaurant(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (error) {
      toast.error('Đã xảy ra lỗi khi xóa!');
    }
  }

  const handleUpdate = async (voucherId: number) => {
    try {
      await updateVoucher(voucherId);
        fetchVouchersRestaurant(restaurantId).then((data) => {
        setVouchers(data);
      });
    } catch (error) {
      toast.error('Đã xảy ra lỗi khi cập nhật!'); 
    }
  };

  const handleAddVoucherDetail = async (data: any) => {
    try {
      await addVoucherDetailRes(data);
      toast.success('Thêm chi tiết voucher thành công!');
      fetchVouchersRestaurant(restaurantId).then((data) => {
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
              <th className="py-3 px-4 text-left">Áp dụng cho</th>
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
                  {voucher.applyType === "ALL" ? (
                    <span className="text-500">Tất cả</span>
                  ) : (
                    <button
                      className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                      onClick={() => {
                        setSelectedVoucher(voucher);
                        setShowModalDetails(true);
                        getFoods();
                      }}
                    >
                      Chi tiết món
                    </button>
                  )}
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
                     (
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
                    Xóa
                  </button>
                  <button
                    className="ml-1 bg-pink-500 text-white px-3 py-1 rounded hover:bg-pink-600"
                    onClick={() => {
                      setShowModalVoucher(true);
                      setSelectedVoucher(voucher);
                    }}
                  >
                    Chi tiết
                  </button>
                  {!voucher.active && (
                    <button
                      className="ml-1 bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
                      onClick={() => {
                        setShowModalDetail(true);
                        setSelectedVoucher(voucher);
                      }}
                    >
                      Sử dụng lại
                    </button>
                  )}
                  {showModalDetail && (
                    <ModalAddDetail
                      voucherId={selectedVoucher?.id}
                      applyType={selectedVoucher?.applyType}
                      foodIds={selectedVoucher?.foodIds}
                      restaurantId={Number(restaurantId)}
                      isOpen={showModalDetail}
                      onClose={() => setShowModalDetail(false)}
                      onSubmit={handleAddVoucherDetail}
                    />
                  )}

                  {selectedVoucher && showModalVoucher && (
                    <ModalVoucher
                      isOpen={showModalVoucher}
                      onClose={() => setShowModalVoucher(false)}
                      voucher={selectedVoucher}
                    />
                  )}

                  {selectedVoucher && showModalDetails && (
                    <ModalDetails
                      isOpen={showModalDetails}
                      onClose={() => setShowModalDetails(false)}
                      voucher={selectedVoucher}
                      foods={foods}
                    />
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center overflow-y-auto">
        <div className="bg-white p-10 rounded-xl w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <h2 className="text-xl font-bold text-green-500 mb-4">Thêm giảm giá</h2>
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
            <label className="block text-sm font-medium text-gray-700">Mô tả</label>
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
            <label className="block text-sm font-medium text-gray-700">Áp dụng cho</label>
            <select
              name="applyType"
              value={formData.applyType}
              onChange={handleChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            >
              <option value="ALL">Tất cả</option>
              <option value="SPECIFIC">Chọn món</option>
            </select>
            {formData.applyType === 'SPECIFIC' && (
  <>
              {/* Thanh tìm kiếm món ăn */}
              <input
                type="text"
                placeholder="Tìm món ăn theo tên..."
                value={searchTerm}
                onChange={handleSearchChange}
                className="mt-1 w-full p-2 mb-1 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              />

              {/* Danh sách món ăn đã lọc */}
              <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 mt-4">
                {filteredFoods.map((food) => (
                  <label
                    key={food.id}
                    className="flex items-center space-x-2 border rounded-lg p-3 hover:bg-gray-50 cursor-pointer"
                  >
                    <input
                      type="checkbox"
                      value={food.id}
                      checked={formData.foodIds?.includes(food.id) || false}
                      onChange={(e) => {
                        const isChecked = e.target.checked;
                        setFormData((prev) => ({
                          ...prev,
                          foodIds: isChecked
                            ? [...(prev.foodIds || []), food.id]
                            : prev.foodIds?.filter((id) => id !== food.id) || [],
                        }));
                      }}
                    />
                    <div>
                      <div className="font-medium">{food.name}</div>
                      <div className="text-sm text-gray-500">{food.price} đ</div>
                    </div>
                  </label>
                ))}
              </div>
            </>
          )}

          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Đơn vị giảm</label>
            <select
              name="type"
              value={formData.type}
              onChange={handleChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            >
              <option value="FIXED">Cố định (VND)</option>
              <option value="PERCENTAGE">Phần trăm (%)</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Giá trị giảm giá ({formData.type === 'FIXED' ? 'VND' : '%'})
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
          <div className="mb-4">
            <label htmlFor="startDate" className="block text-sm font-medium text-gray-700">
              Ngày bắt đầu
            </label>
            <input
              type="datetime-local"
              id="startDate"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            />
          </div>
          <div className="mb-4">
            <label htmlFor="endDate" className="block text-sm font-medium text-gray-700">
              Ngày kết thúc
            </label>
            <input
              type="datetime-local"
              id="endDate"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              required
            />
          </div>
          <div className="mt-6 flex justify-end space-x-2">
            <button
              type="button"
              onClick={() => {
                setIsModalOpen(false);
                setFormData(initialForm);
                setFoods([]);
              }}
              className="bg-gray-300 text-black px-4 py-2 rounded hover:bg-gray-400"
            >
              Thoát
            </button>
            <button
              type="submit"
              className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
            >
              Thêm
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