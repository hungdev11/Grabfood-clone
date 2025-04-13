'use client';

import Image from 'next/image';
import { Button } from '@/components/ui/button';
import { useCart } from '../context/CartContext';
import Link from 'next/link';
import axiosInstance from '@/utils/axiosInstance';
import axios, { AxiosError } from 'axios';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';

export default function Checkout() {
  const { cartId, cartItems, updateQuantity, totalPrice, setCartItems } = useCart();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const cartIdRequest = cartId;
  const [voucherCode, setVoucherCode] = useState<string[]>([]);
  const [voucherCodeApply, setVoucherCodeApply] = useState<string[] | null>(null);
  const [discountedTotal, setDiscountedTotal] = useState<number | null>(null);
  const [voucherError, setVoucherError] = useState<string | null>(null);
  const [vouchers, setVouchers] = useState<any[]>([]);
  const [voucherFetchError, setVoucherFetchError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [orderDetails, setOrderDetails] = useState<any>(null);

  useEffect(() => {
    const fetchVouchers = async () => {
      try {
        const response = await axiosInstance.get('http://localhost:6969/grab/vouchers/checkApply', {
          params: {
            totalPrice: totalPrice,
          },
        });

        if (response.data.code === 200 && response.data.data) {
          setVouchers(response.data.data);
        } else {
          setVoucherFetchError('Không thể tải danh sách mã khuyến mãi.');
        }
      } catch (err) {
        setVoucherFetchError('Lỗi khi tải danh sách mã khuyến mãi. Vui lòng thử lại.');
        console.error('Lỗi khi gọi API mã khuyến mãi:', err);
      }
    };

    fetchVouchers();
  }, [totalPrice]);

  const handleApplyVoucher = async (codes: string[]) => {
    if (!codes || codes.length === 0) {
      setVoucherError('Vui lòng nhập mã khuyến mãi!');
      return;
    }
    setVoucherError(null);
    try {
      const requestBody = {
        listCode: codes,
        totalPrice: totalPrice,
      };

      const response = await axiosInstance.post(
        'http://localhost:6969/grab/order/check/applyVoucher',
        requestBody,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );

      if (response.data.error || !response.data.data) {
        let errorMessage = 'Không thể áp dụng voucher. Vui lòng kiểm tra lại mã!';
        if (response.data.message) {
          const message = response.data.message;
          switch (message) {
            case 'Voucher not found':
              errorMessage = 'Mã voucher không tồn tại!';
              break;
            case 'Voucher expired':
              errorMessage = 'Mã voucher đã hết hạn!';
              break;
            case 'Order price is less than min require':
              errorMessage = 'Đơn hàng không đạt giá trị tối thiểu!';
              break;
            default:
              errorMessage = message;
          }
        }
        setVoucherCodeApply(null);
        setDiscountedTotal(null);
        setVoucherError(errorMessage);
        return;
      }

      const newTotal = response.data.data.newTotalPrice;
      setDiscountedTotal(newTotal);
      setVoucherCodeApply(codes);
      setVoucherCode(codes);
      toast.success(`Đã áp dụng voucher!`);
    } catch (err) {
      setVoucherCodeApply(null);
      setDiscountedTotal(null);
      const error = err as AxiosError<{ message?: string }>;
      let errorMessage = 'Lỗi kết nối hoặc server không phản hồi!';
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      setVoucherError(errorMessage);
    }
  };

  const handleRemoveVoucher = () => {
    setVoucherCodeApply(null);
    setDiscountedTotal(null);
    setVoucherCode([]);
    setVoucherError(null);
    toast.success('Đã xóa mã khuyến mãi!');
  };

  const deliveryFee = 25000;
  const grandTotal = totalPrice + deliveryFee;

  const increaseQuantity = (id: number) => {
    const item = cartItems.find((i) => i.id === id);
    if (item) {
      updateQuantity(id, item.quantity + 1);
    }
    setDiscountedTotal(null);
    setVoucherCodeApply(null);
    setVoucherCode([]);
  };

  const decreaseQuantity = (id: number) => {
    const item = cartItems.find((i) => i.id === id);
    if (item && item.quantity > 1) {
      updateQuantity(id, item.quantity - 1);
    }
    setDiscountedTotal(null);
    setVoucherCodeApply(null);
    setVoucherCode([]);
  };

  const handlePlaceOrder = async () => {
    if (cartItems.length === 0) {
      setError('Giỏ hàng trống. Vui lòng thêm món trước khi đặt.');
      return;
    }

    setLoading(true);
    setError(null);

    const address = "97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7";
    const note = (document.querySelector('input[placeholder="Hãy gõ thêm số tầng"]') as HTMLInputElement)?.value || '';
    const voucherCodes = voucherCodeApply;
    const cartId = cartIdRequest;

    const orderData = {
      cartId,
      address,
      note,
      shippingFee: deliveryFee,
      voucherCode: voucherCodes || [],
    };
    try {
      console.log(orderData)
      const response = await axiosInstance.post('http://localhost:6969/grab/order/create', orderData, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
      console.log(response)

      if (response.data.error) {
        let errorMessage = 'Không thể áp dụng voucher. Vui lòng kiểm tra lại mã!';
        if (response.data.message) {
          const message = response.data.message;
          switch (message) {
            case 'Voucher not found':
              errorMessage = 'Mã voucher không tồn tại!';
              break;
            case 'Voucher expired':
              errorMessage = 'Mã voucher đã hết hạn!';
              break;
            case 'Order price is less than min require':
              errorMessage = 'Đơn hàng không đạt giá trị tối thiểu!';
              break;
            default:
              errorMessage = message;
          }
        }
        return;
      }
      setOrderDetails(response.data);
      setCartItems([]);
      setIsModalOpen(true);
    } catch (err) {
      console.error('Lỗi khi đặt đơn:', err);
      setError('Không thể đặt đơn hàng. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <header className="sticky top-0 z-50 bg-white p-4 shadow-sm">
        <div className="mx-auto flex max-w-7xl items-center justify-between">
          <div className="flex items-center">
            <Image
              src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/screencapture-food-grab-vn-vi-2025-03-09-23_11_48.png-58y4Sx6qPRGdMbIyTzSZeX0sCbpEXq.jpeg"
              alt="GrabFood"
              width={120}
              height={24}
              className="hidden"
            />
            <div className="text-[#00B14F] font-bold text-xl">GrabFood</div>
          </div>
          <div className="flex items-center gap-3">
            <Button variant="outline" className="px-4 py-2 border-gray-300 rounded-md text-gray-700 hover:bg-gray-100" asChild>
              <Link href="/login">Login/Sign Up</Link>
            </Button>
          </div>
        </div>
      </header>

      <div className="container mx-auto p-4 max-w-2xl">
        <h1 className="text-2xl font-bold mb-2">Bước cuối cùng - Thanh toán</h1>

        <div className="bg-white p-4 rounded-lg shadow-md mb-4">
          <h2 className="text-lg font-semibold mb-3">Giao đến</h2>
          <p className="text-sm text-gray-600 mb-3">Delivery arrival time: 25 min (3.4 km away)</p>
          <div className="flex gap-4">
            <div className="w-1/2">
              <Image
                src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Google_Maps_Logo_2020.svg/533px-Google_Maps_Logo_2020.svg.png"
                alt="Delivery Map"
                width={150}
                height={150}
                className="rounded-md w-full h-auto"
              />
            </div>
            <div className="w-1/2 flex flex-col gap-3">
              <div>
                <label className="text-base font-medium text-gray-700">Địa chỉ</label>
                <input
                  type="text"
                  value="97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7"
                  className="w-full p-2 border rounded-md text-base mt-1"
                  readOnly
                />
              </div>
              <div>
                <label className="text-base font-medium text-gray-700">Chi tiết địa chỉ</label>
                <input
                  type="text"
                  value="97 Man Thiện - 97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 70000, Vietnam"
                  className="w-full p-2 border rounded-md text-base mt-1 text-red-500"
                  readOnly
                />
              </div>
              <div>
                <label className="text-base font-medium text-gray-700">Ghi chú cho tài xế</label>
                <input
                  id='note'
                  type="text"
                  placeholder="Hãy gõ thêm số tầng"
                  className="w-full p-2 border rounded-md text-base mt-1"
                />
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white p-4 rounded-lg shadow-md mb-4">
          <h2 className="text-lg font-semibold mb-3">Tóm tắt đơn hàng</h2>
          {cartItems.map((item) => (
            <div key={item.id} className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Image src={item.image} alt={item.foodName} width={50} height={50} className="rounded-md object-cover" />
                <div>
                  <h3 className="text-base font-medium">{item.foodName}</h3>
                  <div className="text-xs text-gray-600">
                    {item.additionFoods.map((food) => (
                      <p key={food.id}>+ {food.name}</p>
                    ))}
                  </div>
                  <div className="flex items-center gap-1 mt-1">
                    <button
                      className="w-6 h-6 flex items-center justify-center bg-gray-200 rounded-full text-gray-600"
                      onClick={() => decreaseQuantity(item.id)}
                    >
                      -
                    </button>
                    <span className="text-sm">{item.quantity}</span>
                    <button
                      className="w-5 h-5 flex items-center justify-center bg-gray-200 rounded-full text-gray-600 text-sm"
                      onClick={() => increaseQuantity(item.id)}
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>
              <p className="text-base font-semibold">{(
                (item.price + item.additionFoods.reduce((sum, add) => sum + add.price, 0)) *
                item.quantity
              ).toLocaleString()} đ</p>
            </div>
          ))}
          <div className="border-t pt-3 mt-3">
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>Tổng tạm tính</span>
              <span>{totalPrice.toLocaleString()} đ</span>
            </div>
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>Phí ứng dụng <span className="text-blue-500 cursor-pointer">ⓘ</span></span>
              <span>{deliveryFee.toLocaleString()} đ</span>
            </div>
          </div>
        </div>

        <div className="bg-white p-4 rounded-lg shadow-md mb-4">
          <h2 className="text-lg font-semibold mb-3">Chi tiết thanh toán</h2>
          <label className="text-base font-medium text-gray-700">Phương thức thanh toán</label>
          <select className="w-full p-2 border rounded-md text-base mb-3 mt-1">
            <option value="cash">Tiền mặt</option>
            <option value="card">Thẻ tín dụng</option>
          </select>
          <label className="text-base font-medium text-gray-700">Hồ sơ</label>
          <select className="w-full p-2 border rounded-md text-base mb-3 mt-1">
            <option value="personal">Personal</option>
          </select>
        </div>

        <div className="bg-white p-4 rounded-lg shadow-md mb-20">
          <h2 className="text-lg font-semibold mb-3">Khuyến mãi</h2>
          <div className="flex items-center gap-2 mb-4">
            <input
              type="text"
              placeholder="Nhập mã khuyến mãi (cách nhau bằng dấu phẩy)"
              className="w-full p-2 border rounded-md text-base"
              value={voucherCode.join(', ')}
              onChange={(e) => {
                const codes = e.target.value.split(',').map(code => code.trim()).filter(code => code);
                setVoucherCode(codes);
              }}
            />
            <Button
              className="bg-gray-300 text-black hover:bg-gray-400 px-3 py-1 text-base"
              onClick={() => handleApplyVoucher(voucherCode)}
            >
              Áp dụng
            </Button>
          </div>
          {voucherError && (
            <p className="text-red-500 text-sm mt-2 mb-4">{voucherError}</p>
          )}

          <div className="relative">
            {voucherFetchError && (
              <p className="text-red-500 text-sm mb-4">{voucherFetchError}</p>
            )}
            {vouchers.length === 0 && !voucherFetchError && (
              <p className="text-gray-600 text-sm mb-4">Không có mã khuyến mãi nào khả dụng.</p>
            )}
            {vouchers.length > 0 && (
              <div className="flex gap-4 overflow-x-auto pb-4">
                {vouchers.map((voucher) => (
                  <div key={voucher.id} className="min-w-[250px] border rounded-lg p-4 shadow-sm">
                    <h3 className="text-base font-semibold text-gray-800">
                      {voucher.description}
                    </h3>
                    <p className="text-sm text-gray-600 mt-1">
                      Mã khuyến mãi: <span className="font-medium">{voucher.code}</span>
                    </p>
                    <p className="text-sm text-gray-600">
                      Giá trị: {voucher.type === 'FIXED' ? `${voucher.value.toLocaleString()} đ` : `${voucher.value}%`}
                    </p>
                    <p className="text-sm text-gray-600">
                      Đơn tối thiểu: {voucher.minRequire.toLocaleString()} đ
                    </p>
                    <div className="mt-3 flex justify-end">
                      {voucherCodeApply?.includes(voucher.code) ? (
                        <Button
                          variant="ghost"
                          className="text-red-500 hover:text-red-700 text-sm font-medium p-0"
                          onClick={handleRemoveVoucher}
                        >
                          Xóa
                        </Button>
                      ) : (
                        <Button
                          className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 text-sm"
                          onClick={() => {
                            const newCodes = [...voucherCode, voucher.code];
                            setVoucherCode(newCodes);
                            handleApplyVoucher(newCodes);
                          }}
                        >
                          Áp dụng
                        </Button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {error && <p className="text-red-500 text-center mb-4">{error}</p>}
      </div>

      <div className="fixed bottom-0 left-0 right-0 bg-white p-4 shadow-lg border-t border-gray-200 flex justify-between items-center max-w-2xl mx-auto">
        <div>
          <p className="text-lg font-bold">Tổng cộng</p>
          <p className="text-sm text-gray-500">
            {(discountedTotal !== null ? discountedTotal + deliveryFee : grandTotal).toLocaleString()} đ{' '}
            {discountedTotal !== null && (
              <span className="line-through">{grandTotal.toLocaleString()} đ</span>
            )}
          </p>
        </div>
        <Button
          id="order-button"
          className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 text-base"
          onClick={handlePlaceOrder}
          disabled={loading}
        >
          {loading ? 'Đang xử lý...' : 'Đặt đơn'}
        </Button>
      </div>

      <Dialog open={isModalOpen} onOpenChange={(open) => {
        if (!open) {
          return;
        }
        setIsModalOpen(open);
      }}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Đặt hàng thành công!</DialogTitle>
          </DialogHeader>
          <div className="max-h-[80vh] overflow-y-auto">
            {orderDetails && (
              <div className="space-y-4">
                <div>
                  <h3 className="font-semibold">Thông tin đơn hàng</h3>
                  <p><strong>Trạng thái:</strong> {orderDetails.status}</p>
                  <p><strong>Tổng tiền:</strong> {orderDetails.totalPrice.toLocaleString()} đ</p>
                  <p><strong>Phí giao hàng:</strong> {orderDetails.shippingFee.toLocaleString()} đ</p>
                  <p><strong>Địa chỉ:</strong> {orderDetails.address}</p>
                  <p><strong>Ghi chú:</strong> {orderDetails.note || 'Không có'}</p>
                </div>
              </div>
            )}
          </div>
          <DialogFooter>
            <Button
              onClick={() => {
                setIsModalOpen(false);
                router.push('/');
              }}
            >
              Đóng
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <ToastContainer />
    </div>
  );
}