import { Voucher } from "../types/voucher";

export default function ModalVoucher({ isOpen, onClose, voucher}: { isOpen: boolean; onClose: () => void; voucher: Voucher }) {
    if (!isOpen || !voucher) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div className="bg-white rounded-xl shadow-lg w-full max-w-md p-6 relative">
        <h2 className="text-xl font-bold mb-4">Thông tin Voucher</h2>

        <div className="space-y-2">
          <p><strong>Mã:</strong> {voucher.code}</p>
          <p><strong>Mô tả:</strong> {voucher.description}</p>
          <p><strong>Giá trị:</strong> {voucher.value} {voucher.type === 'PERCENTAGE' ? '%' : 'VNĐ'}</p>
          <p><strong>Điều kiện tối thiểu:</strong> {voucher.minRequire.toLocaleString()} VNĐ</p>
          <p><strong>Loại áp dụng:</strong> {voucher.applyType}</p>
          {voucher.endTime && (
            <p><strong>Thời gian hết hạn:</strong> {voucher.endTime}</p>
          )}
          <p><strong>Trạng thái:</strong> {voucher.status}</p>
          <p><strong>Đang hoạt động:</strong> {voucher.active ? 'Có' : 'Không'}</p>
        </div>

        <button
          className="absolute top-2 right-2 text-gray-500 hover:text-black text-xl"
          onClick={onClose}
        >
          &times;
        </button>
      </div>
    </div>
  );
}