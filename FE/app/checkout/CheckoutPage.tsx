"use client";

import Image from "next/image";
import { Button } from "@/components/ui/button";
import { useCart } from "../context/CartContext";
import axiosInstance from "@/utils/axiosInstance";
import axios, { AxiosError } from "axios";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Header from "@/components/header";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { CartProvider } from "../context/CartContext";
import { set } from "date-fns";
import { de } from "date-fns/locale";

export default function Checkout() {
  const { cartId, cartItems, updateQuantity, totalPrice, setCartItems } =
    useCart();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const [isRestaurantOpen, setIsRestaurantOpen] = useState<boolean | null>(
    true
  );
  const [showModal, setShowModal] = useState(false);

  const cartIdRequest = cartId;
  //Address
  const [addresses, setAddresses] = useState<any[]>([]);
  const [selectedAddress, setSelectedAddress] = useState<any>(null);
  const [addressLoading, setAddressLoading] = useState<boolean>(false);
  const [showAddressSelector, setShowAddressSelector] =
  useState<boolean>(false);

    //check xem khoảng cách có < 10km không?
  const [isDistanceOk, setIsDistanceOk] = useState<boolean>(false);

  const [deliveryFee, setDeliveryFee] = useState<number>(13000);
  const grandTotal = totalPrice + deliveryFee;
  const fetchAddresses = async () => {
    try {
      setAddressLoading(true);
      const userId = localStorage.getItem("grabUserId");
      if (!userId) throw new Error("User ID not found");

      const response = await axiosInstance.get(
        `http://localhost:6969/grab/users/${userId}/addresses`
      );

      if (response.data) {
        setAddresses(response.data);

        // Set default address as selected
        const defaultAddress = response.data.find(
          (addr: any) => addr.default || addr.isDefault
        );

        if (defaultAddress) {
          setSelectedAddress(defaultAddress);
          console.log("Default address:", defaultAddress);
        } else if (response.data.length > 0) {
          // If no default, use first address
          setSelectedAddress(response.data[0]);
        }
        checkDistance(
          userId,
          defaultAddress?.lat || response.data[0]?.lat,
          defaultAddress?.lon || response.data[0]?.lon  )
      }
    } catch (error) {
      console.error("Error fetching addresses:", error);
      toast.error("Không thể tải địa chỉ. Vui lòng thử lại.");
    } finally {
      setAddressLoading(false);
    }
  };
  useEffect(() => {
    fetchAddresses();
  }, []);

  useEffect(() => {
    if (selectedAddress) {
      const userId = localStorage.getItem("grabUserId");
      if (!userId) throw new Error("User ID not found");
      checkDistance(userId, selectedAddress.lat, selectedAddress.lon);
    }
  }
  , [selectedAddress]);

  const [distance, setDistance] = useState<number>(0);
  const [duration, setDuration] = useState<number>(0);

    const checkDistance = async (userId: any,lat: any, lon: any) => {
      if (lat === undefined || lon === undefined) {
        alert("Vui lòng chọn địa chỉ giao hàng.");
        setIsDistanceOk(false);
        return;
      }
      try {
        const response = await axiosInstance.get(
          "http://localhost:6969/grab/order/checkDistance",
          {
            params: {
              userId: userId,
              lat: lat,
              lon: lon
            },
          }
        );
        console.log("Khoảng cách:", response.data);
        if (response.data.code === 200) {
          if (response.data.data.check === false) {
            toast.error("Khoảng cách quá xa, không thể giao hàng.");
            setDistance(0);
            setDuration(0);
            setDeliveryFee(0);
          } else {
            setDistance(response.data.data.distance);
            setDuration(response.data.data.duration);
            setDeliveryFee(response.data.data.shippingFee);
          }
          setIsDistanceOk(response.data.data.check);
          //console.log("Khoảng cách:", response.data.data);
        }
      } catch (error) {  
        console.error("Lỗi kiểm tra khoảng cách:", error);
        toast.error("Lỗi kiểm tra khoảng cách. Vui lòng thử lại.");
      }
    };

  // Add this dialog component
  const AddressSelector = ({
    open,
    onClose,
  }: {
    open: boolean;
    onClose: () => void;
  }) => {
    return (
      <Dialog open={open} onOpenChange={onClose}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Chọn địa chỉ giao hàng</DialogTitle>
          </DialogHeader>

          <div className="max-h-[60vh] overflow-y-auto space-y-3">
            {addressLoading ? (
              <p className="text-center py-4">Đang tải địa chỉ...</p>
            ) : addresses.length === 0 ? (
              <p className="text-center py-4">Bạn chưa có địa chỉ nào</p>
            ) : (
              addresses.map((address) => (
                <div
                  key={address.id}
                  className={`border p-3 rounded-md cursor-pointer hover:bg-gray-50 ${
                    selectedAddress?.id === address.id
                      ? "border-green-500 bg-green-50"
                      : ""
                  }`}
                  onClick={() => {
                    setSelectedAddress(address);
                    onClose();
                  }}
                >
                  <p className="font-medium">{address.detail}</p>
                  <p className="text-sm text-gray-600">{address.displayName}</p>
                  {(address.default || address.isDefault) && (
                    <span className="text-xs text-green-600 font-medium">
                      Mặc định
                    </span>
                  )}
                </div>
              ))
            )}
          </div>

          <DialogFooter>
            <Button onClick={() => window.open("/account", "_blank")}>
              Thêm địa chỉ mới
            </Button>
            <Button variant="outline" onClick={onClose}>
              Đóng
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  };

  //voucher
  const [discountOrderPrice, setDiscountOrderPrice] = useState<number>(0);
  const [discountShippingPrice, setDiscountShippingPrice] = useState<number>(0);
  const [newOrderPrice, setNewOrderPrice] = useState<number | null>(null);
  const [newShippingFee, setNewShippingFee] = useState<number | null>(null);

  const [voucherCode, setVoucherCode] = useState<string[]>([]);
  const [voucherCodeApply, setVoucherCodeApply] = useState<string[] | null>(
    null
  );
  const [voucherError, setVoucherError] = useState<string | null>(null);
  const [voucherTotal, setVoucherTotal] = useState<number | null>(null);

  //show voucher
  const [vouchers, setVouchers] = useState<any[]>([]);
  const [voucherFetchError, setVoucherFetchError] = useState<string | null>(
    null
  );

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [orderDetails, setOrderDetails] = useState<any>(null);

  //order
  const [orderId, setOrderId] = useState<number | null>(null);

  // state lưu payment_method
  const [paymentMethod, setPaymentMethod] = useState<string>("cod");

  

  const handlePaymentMethodChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setPaymentMethod(event.target.value);
    console.log("Selected payment method:", paymentMethod);
  };

  const checkRestaurantOpen = async () => {
    try {
      const response = await axiosInstance.get(
        "http://localhost:6969/grab/cart/checkOpen",
        {
          params: {
            cartId: cartIdRequest,
          },
        }
      );
      setIsRestaurantOpen(response.data);
      console.log("Restaurant open status:", response.data);
    } catch (error) {
      console.error("Lỗi kiểm tra nhà hàng:", error);
      setIsRestaurantOpen(false); // fallback nếu lỗi
    }
  };

  useEffect(() => {
    const fetchVouchers = async () => {
      try {
        const response = await axiosInstance.get(
          "http://localhost:6969/grab/vouchers/checkApply",
          {
            params: {
              totalPrice: totalPrice,
            },
          }
        );

        if (response.data.code === 200 && response.data.data) {
          setVouchers(response.data.data);
        } else {
          setVoucherFetchError("Không thể tải danh sách mã khuyến mãi.");
        }
      } catch (err) {
        setVoucherFetchError(
          "Lỗi khi tải danh sách mã khuyến mãi. Vui lòng thử lại."
        );
        console.error("Lỗi khi gọi API mã khuyến mãi:", err);
      }
    };

    fetchVouchers();
  }, [totalPrice]);

  const handleApplyVoucher = async (codes: string[]) => {
    if (!codes || codes.length === 0) {
      setVoucherError("Vui lòng nhập mã khuyến mãi!");
      return;
    }
    setVoucherError(null);
    try {
      const requestBody = {
        listCode: codes,
        totalPrice: totalPrice,
        shippingFee: deliveryFee,
      };
      console.log(requestBody);
      const response = await axiosInstance.post(
        "http://localhost:6969/grab/order/check/applyVoucher",
        requestBody,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response.data);
      if (response.data.error || !response.data.data) {
        let errorMessage =
          "Không thể áp dụng voucher. Vui lòng kiểm tra lại mã!";
        if (response.data.message) {
          const message = response.data.message;
          switch (message) {
            case "Voucher not found":
              errorMessage = "Mã voucher không tồn tại!";
              break;
            case "Voucher expired":
              errorMessage = "Mã voucher đã hết hạn!";
              break;
            case "Order price is less than min require":
              errorMessage = "Đơn hàng không đạt giá trị tối thiểu!";
              break;
            default:
              errorMessage = message;
          }
        }
        setVoucherCodeApply(null);
        setVoucherError(errorMessage);
        return;
      }

      const newOrderPrice = response.data.data.newOrderPrice;
      const newShippingFee = response.data.data.newShippingFee;
      const discountOrderPrice = response.data.data.discountOrderPrice;
      const discountShippingPrice = response.data.data.discountShippingPrice;
      console.log(
        newOrderPrice,
        newShippingFee,
        discountOrderPrice,
        discountShippingPrice
      );
      setNewOrderPrice(newOrderPrice);
      setNewShippingFee(newShippingFee);
      setDiscountOrderPrice(discountOrderPrice);
      setDiscountShippingPrice(discountShippingPrice);
      setVoucherCodeApply(codes);
      setVoucherCode(codes);
      toast.success(`Đã áp dụng voucher!`);
    } catch (err) {
      setVoucherCodeApply(null);
      const error = err as AxiosError<{ message?: string }>;
      let errorMessage = "Lỗi kết nối hoặc server không phản hồi!";
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      setVoucherError(errorMessage);
    }
  };

  const handleRemoveVoucher = (codeToRemove: string) => {
    // Lọc bỏ mã voucher được chọn
    const updatedCodes = voucherCode.filter((code) => code !== codeToRemove);

    // Cập nhật state
    setVoucherCode(updatedCodes);
    setVoucherCodeApply(updatedCodes.length > 0 ? updatedCodes : null);
    setVoucherError(null);

    // Nếu không còn mã nào, reset các giá trị giảm giá
    if (updatedCodes.length === 0) {
      setNewOrderPrice(null);
      setNewShippingFee(null);
      setDiscountOrderPrice(0);
      setDiscountShippingPrice(0);
      setVoucherTotal(null);
    } else {
      // Gọi lại API để áp dụng các mã còn lại
      handleApplyVoucher(updatedCodes);
    }

    toast.success(`Đã xóa mã khuyến mãi ${codeToRemove}!`);
  };



  const increaseQuantity = (id: number) => {
    const item = cartItems.find((i) => i.id === id);
    if (item) {
      updateQuantity(id, item.quantity + 1);
    }
    setVoucherCodeApply(null);
    setVoucherCode([]);
    setDiscountOrderPrice(0);
    setDiscountShippingPrice(0);
  };

  const decreaseQuantity = (id: number) => {
    const item = cartItems.find((i) => i.id === id);
    if (item && item.quantity > 1) {
      updateQuantity(id, item.quantity - 1);
    }
    setVoucherCodeApply(null);
    setVoucherCode([]);
    setDiscountOrderPrice(0);
    setDiscountShippingPrice(0);
  };

  const handlePlaceOrder = async () => {
    if (cartItems.length === 0) {
      setError("Giỏ hàng trống. Vui lòng thêm món trước khi đặt.");
      return;
    }
    if (!selectedAddress) {
      setError("Vui lòng chọn địa chỉ giao hàng.");
      return;
    }
    setLoading(true);
    setError(null);

    const address = `${selectedAddress.detail}, ${selectedAddress.displayName}`;
    const note =
      (
        document.querySelector(
          'input[placeholder="Hãy gõ thêm số tầng"]'
        ) as HTMLInputElement
      )?.value || "";
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
      console.log(orderData);
      const response = await axiosInstance.post(
        "http://localhost:6969/grab/payments/cod",
        orderData,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response);

      if (response.data.error) {
        let errorMessage =
          "Không thể áp dụng voucher. Vui lòng kiểm tra lại mã!";
        if (response.data.message) {
          const message = response.data.message;
          switch (message) {
            case "Voucher not found":
              errorMessage = "Mã voucher không tồn tại!";
              break;
            case "Voucher expired":
              errorMessage = "Mã voucher đã hết hạn!";
              break;
            case "Order price is less than min require":
              errorMessage = "Đơn hàng không đạt giá trị tối thiểu!";
              break;
            default:
              errorMessage = message;
          }
        }
        return;
      }
      const apiResponse = response.data.data;
      setOrderId(apiResponse.orderId);
      console.log("orderId", orderId);
      setOrderDetails(apiResponse);
      setCartItems([]);
      setIsModalOpen(true);
    } catch (err) {
      console.error("Lỗi khi đặt đơn:", err);
      setError("Không thể đặt đơn hàng. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  //test
  const handlePaymentMomo = async () => {
    setLoading(true);
    setError(null);
    const address = `${selectedAddress.detail}, ${selectedAddress.displayName}`;
    const note =
      (
        document.querySelector(
          'input[placeholder="Hãy gõ thêm số tầng"]'
        ) as HTMLInputElement
      )?.value || "";
    const voucherCodes = voucherCodeApply;
    const cartId = cartIdRequest;

    const orderData = {
      cartId,
      address,
      note,
      shippingFee: deliveryFee ,
      voucherCode: voucherCodes || [],
    };
    try {
      console.log(orderData);
      const response = await axiosInstance.post(
        "http://localhost:6969/grab/payments/momo",
        orderData,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response.data);
      const payurl = response.data;
      console.log(payurl);
      if (payurl) {
        window.location.href = payurl;
      } else {
        setError("Không tạo được mã thanh toán");
      }
    } catch (error) {
      if (error instanceof Error) {
        setError("Lỗi khi khởi tạo thanh toán: " + error.message);
      } else {
        setError("Lỗi khi khởi tạo thanh toán: Không xác định.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handlePaymentVNPay = async () => {
    setLoading(true);
    setError(null);
    const address = `${selectedAddress.detail}, ${selectedAddress.displayName}`;
    const note =
      (
        document.querySelector(
          'input[placeholder="Hãy gõ thêm số tầng"]'
        ) as HTMLInputElement
      )?.value || "";
    const voucherCodes = voucherCodeApply;
    const cartId = cartIdRequest;

    const orderData = {
      cartId,
      address,
      note,
      shippingFee: deliveryFee,
      voucherCode: voucherCodes || [],
      bankCode: "NCB",
    };
    try {
      console.log(orderData);
      const response = await axiosInstance.post(
        "http://localhost:6969/grab/payments/vnpay",
        orderData,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response.data);
      const payurl = response.data;
      console.log(payurl);
      if (payurl) {
        window.location.href = payurl;
      } else {
        setError("Không tạo được mã thanh toán");
      }
    } catch (error) {
      if (error instanceof Error) {
        setError("Lỗi khi khởi tạo thanh toán: " + error.message);
      } else {
        setError("Lỗi khi khởi tạo thanh toán: Không xác định.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleOrderButton = async () => {
    try {
      const response = await axiosInstance.get(
        "http://localhost:6969/grab/cart/checkOpen",
        {
          params: { cartId: cartIdRequest },
        }
      );

      console.log("Restaurant open status:", response.data);
      if (response.data === true) {
        if (paymentMethod === "cod") {
          handlePlaceOrder();
        } else if (paymentMethod === "momo") {
          handlePaymentMomo();
        } else if (paymentMethod === "vnpay") {
          handlePaymentVNPay();
        }
      } else {
        setShowModal(true);
      }
    } catch (error) {
      console.error("Lỗi kiểm tra nhà hàng:", error);
      setShowModal(true);
    }
  };

  const formatDistance = (distance: number) => {
    if (distance < 1000) {
      return `${distance} m`;
    } else {
      return `${(distance / 1000).toFixed(1)} km`;
    }
  }

  const formatDuration = (duration: number) => {
    const minutes = Math.floor(duration / 60) + 10 ;
    return minutes
  }

  return (
    <CartProvider>
      <div>
        <Header />
        <div className="container mx-auto p-4 max-w-2xl">
          <h1 className="text-2xl font-bold mb-2">
            Bước cuối cùng - Thanh toán
          </h1>

          <div className="bg-white p-4 rounded-lg shadow-md mb-4">
            <h2 className="text-lg font-semibold mb-3">Giao đến</h2>
            <p className="text-sm text-gray-600 mb-3">
              Thời gian giao dự kiến: {duration > 0 ? formatDuration(duration): '0'} phút ({formatDistance(distance)})
            </p>
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
                  <div className="flex justify-between items-center mb-1">
                    <label className="text-base font-medium text-gray-700">
                      Địa chỉ
                    </label>
                    <button
                      className="text-green-600 text-sm font-medium"
                      onClick={() => setShowAddressSelector(true)}
                    >
                      Thay đổi
                    </button>
                  </div>

                  {addressLoading ? (
                    <div className="w-full p-2 border rounded-md text-base mt-1 bg-gray-100">
                      Đang tải...
                    </div>
                  ) : selectedAddress ? (
                    <div className="w-full p-2 border rounded-md text-base mt-1">
                      <p>{selectedAddress.detail}</p>
                      <p className="text-sm text-gray-600">
                        {selectedAddress.displayName}
                      </p>
                    </div>
                  ) : (
                    <div
                      className="w-full p-2 border rounded-md text-base mt-1 text-blue-600 cursor-pointer flex items-center justify-center"
                      onClick={() => setShowAddressSelector(true)}
                    >
                      + Chọn địa chỉ giao hàng
                    </div>
                  )}
                </div>

                <div>
                  <label className="text-base font-medium text-gray-700">
                    Ghi chú cho tài xế
                  </label>
                  <input
                    id="note"
                    type="text"
                    placeholder="Hãy gõ thêm số tầng"
                    className="w-full p-2 border rounded-md text-base mt-1"
                  />
                </div>
              </div>
            </div>

            {/* Add the address selector dialog */}
            <AddressSelector
              open={showAddressSelector}
              onClose={() => setShowAddressSelector(false)}
            />
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md mb-4">
            <h2 className="text-lg font-semibold mb-3">Tóm tắt đơn hàng</h2>
            {cartItems.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between mb-3"
              >
                <div className="flex items-center gap-2">
                  <Image
                    src={item.image}
                    alt={item.foodName}
                    width={50}
                    height={50}
                    className="rounded-md object-cover"
                  />
                  <div>
                    <h3 className="text-base font-medium">{item.foodName}</h3>
                    <div className="text-xs text-gray-600">
                      {item.additionalFoods.map((food) => (
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
                <p className="text-base font-semibold">
                  {(
                    (item.price +
                      item.additionalFoods.reduce(
                        (sum, add) => sum + add.price,
                        0
                      )) *
                    item.quantity
                  ).toLocaleString()}{" "}
                  đ
                </p>
              </div>
            ))}
            <div className="border-t pt-3 mt-3">
              <div className="flex justify-between text-sm text-gray-600 mb-1">
                <span>Tổng tạm tính</span>
                <span>{totalPrice.toLocaleString()} đ</span>
              </div>
              <div className="flex justify-between text-sm text-gray-600 mb-1">
                <span>
                  Phí vận chuyển{" "}
                  <span className="text-blue-500 cursor-pointer">ⓘ</span>
                </span>
                <span>{deliveryFee.toLocaleString()} đ</span>
              </div>
              {(discountOrderPrice ?? 0) !== 0 && (
                <div className="flex justify-between text-sm text-gray-600 mb-1">
                  <span>
                    Giảm giá sản phẩm
                    <span className="text-blue-500 cursor-pointer"></span>
                  </span>
                  <span>- {discountOrderPrice.toLocaleString()} đ</span>
                </div>
              )}
              {(discountShippingPrice ?? 0) !== 0 && (
                <div className="flex justify-between text-sm text-gray-600 mb-1">
                  <span>
                    Giảm giá vận chuyển
                    <span className="text-blue-500 cursor-pointer"></span>
                  </span>
                  <span>- {discountShippingPrice.toLocaleString()} đ</span>
                </div>
              )}
            </div>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md mb-4">
            <h2 className="text-lg font-semibold mb-5">
              Phương thức thanh toán
            </h2>

            {/* Option COD */}
            <div className="flex items-center mb-5">
              <input
                type="radio"
                name="payment-method"
                value="cod"
                className="mr-3"
                checked={paymentMethod === "cod"}
                onChange={handlePaymentMethodChange}
              />
              <div className="flex items-center">
                <img
                  src="/logo/logoCOD.png"
                  alt="COD"
                  className="w-6 h-6 mr-3"
                />
                <label className="text-base font-medium text-gray-700">
                  Thanh toán khi nhận hàng
                </label>
              </div>
            </div>

            {/* Option VnPay */}
            <div className="flex items-center mb-5">
              <input
                type="radio"
                name="payment-method"
                value="vnpay"
                className="mr-3"
                checked={paymentMethod === "vnpay"}
                onChange={handlePaymentMethodChange}
              />
              <div className="flex items-center">
                <img
                  src="/logo/logoVNPay.jpg"
                  alt="VnPay"
                  className="w-6 h-6 mr-3"
                />
                <label className="text-base font-medium text-gray-700">
                  VN Pay
                </label>
              </div>
            </div>

            {/* Option MoMo */}
            <div className="flex items-center mb-5">
              <input
                type="radio"
                name="payment-method"
                value="momo"
                className="mr-3"
                checked={paymentMethod === "momo"}
                onChange={handlePaymentMethodChange}
              />
              <div className="flex items-center">
                <img
                  src="https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png"
                  alt="MoMo"
                  className="w-6 h-6 mr-3"
                />
                <label className="text-base font-medium text-gray-700">
                  Ví điện tử MoMo
                </label>
              </div>
            </div>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md mb-20">
            <h2 className="text-lg font-semibold mb-3">Khuyến mãi</h2>
            <div className="flex items-center gap-2 mb-4">
              <input
                type="text"
                placeholder="Nhập mã khuyến mãi (cách nhau bằng dấu phẩy)"
                className="w-full p-2 border rounded-md text-base"
                value={voucherCode.join(", ")}
                onChange={(e) => {
                  const codes = e.target.value
                    .split(",")
                    .map((code) => code.trim())
                    .filter((code) => code);
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
                <p className="text-gray-600 text-sm mb-4">
                  Không có mã khuyến mãi nào khả dụng.
                </p>
              )}
              {vouchers.length > 0 && (
                <div className="flex gap-4 overflow-x-auto pb-4">
                  {vouchers.map((voucher) => (
                    <div
                      key={voucher.id}
                      className="min-w-[250px] border rounded-lg p-4 shadow-sm"
                    >
                      <h3 className="text-base font-semibold text-gray-800">
                        {voucher.description}
                      </h3>
                      <p className="text-sm text-gray-600 mt-1">
                        Mã khuyến mãi:{" "}
                        <span className="font-medium">{voucher.code}</span>
                      </p>
                      <p className="text-sm text-gray-600">
                        Giá trị:{" "}
                        {voucher.type === "FIXED"
                          ? `${voucher.value.toLocaleString()} đ`
                          : `${voucher.value}%`}
                      </p>
                      <p className="text-sm text-gray-600">
                        Đơn tối thiểu: {voucher.minRequire.toLocaleString()} đ
                      </p>
                      <p className="text-sm text-gray-600">
                        Loại: {voucher.applyType}
                      </p>
                      <div className="mt-3 flex justify-end">
                        {voucherCodeApply?.includes(voucher.code) ? (
                          <Button
                            variant="ghost"
                            className="text-red-500 hover:text-red-700 text-sm font-medium p-0"
                            onClick={() => handleRemoveVoucher(voucher.code)}
                          >
                            Xóa
                          </Button>
                        ) : (
                          <Button
                            className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 text-sm"
                            onClick={() => {
                              // Lấy danh sách mã hiện tại
                              const currentCodes = [...voucherCode];
                              // Tìm voucher được chọn
                              const selectedVoucher = vouchers.find(
                                (v) => v.code === voucher.code
                              );
                              if (!selectedVoucher) return;

                              // Xóa các mã cùng loại (SHIPPING hoặc ORDER)
                              const filteredCodes = currentCodes.filter(
                                (code) => {
                                  const existingVoucher = vouchers.find(
                                    (v) => v.code === code
                                  );
                                  return (
                                    !existingVoucher ||
                                    existingVoucher.applyType !==
                                      selectedVoucher.applyType
                                  );
                                }
                              );

                              // Thêm mã mới
                              const newCodes = [...filteredCodes, voucher.code];
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
              {((discountOrderPrice ?? 0) !== 0 ||
              (discountShippingPrice ?? 0) !== 0
                ? (newOrderPrice ?? 0) + (newShippingFee ?? 0)
                : grandTotal
              ).toLocaleString()}{" "}
              đ{" "}
              {((discountOrderPrice ?? 0) !== 0 ||
                (discountShippingPrice ?? 0)) !== 0 && (
                <span className="line-through">
                  {grandTotal.toLocaleString()} đ
                </span>
              )}
            </p>
          </div>
          <Button
            id="order-button"
            className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 text-base"
            onClick={handleOrderButton}
            disabled={loading || !isDistanceOk}
          >
            {loading ? "Đang xử lý..." : "Đặt đơn"}
          </Button>
        </div>

        {showModal === true && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-md text-center max-w-sm">
              <h2 className="text-lg font-bold mb-4">Ngoài giờ bán hàng</h2>
              <p className="text-sm text-gray-600 mb-4">
                Nhà hàng hiện không nhận đơn. Vui lòng quay lại sau.
              </p>
              <Button
                className="bg-blue-500 hover:bg-blue-600 text-white"
                onClick={() => (window.location.href = "/")}
              >
                Đóng
              </Button>
            </div>
          </div>
        )}

        <Dialog
          open={isModalOpen}
          onOpenChange={(open) => {
            if (!open) {
              return;
            }
            setIsModalOpen(open);
          }}
        >
          <DialogContent className="max-w-lg">
            <DialogHeader>
              <DialogTitle>Đặt hàng thành công!</DialogTitle>
            </DialogHeader>
            <div className="max-h-[80vh] overflow-y-auto">
              {orderDetails && (
                <div className="space-y-4">
                  <div>
                    <h3 className="font-semibold">Thông tin đơn hàng</h3>
                    <p>
                      <strong>Trạng thái:</strong> {orderDetails.status}
                    </p>
                    <p>
                      <strong>Tổng tiền:</strong>{" "}
                      {orderDetails.totalPrice.toLocaleString()} đ
                    </p>
                    <p>
                      <strong>Phí giao hàng:</strong>{" "}
                      {orderDetails.shippingFee.toLocaleString()} đ
                    </p>
                    <p>
                      <strong>Địa chỉ:</strong> {orderDetails.address}
                    </p>
                    <p>
                      <strong>Ghi chú:</strong>{" "}
                      {orderDetails.note || "Không có"}
                    </p>
                  </div>
                </div>
              )}
            </div>
            <DialogFooter>
              <Button
                onClick={() => {
                  setIsModalOpen(false);
                  router.push("/");
                }}
              >
                Đóng
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
        <ToastContainer />
      </div>
    </CartProvider>
  );
}
