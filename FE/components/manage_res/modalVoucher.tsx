import { Voucher } from "../types/voucher";

export default function ModalVoucher({ isOpen, onClose, voucher}: { isOpen: boolean; onClose: () => void; voucher: Voucher }) {
    if (!isOpen || !voucher) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div className="bg-white rounded-xl shadow-lg w-full max-w-md p-6 relative">
        {/* Badge trạng thái hiệu lực */}
        <div className={`absolute top-2 left-2 px-3 py-1 rounded-full text-sm font-semibold
          ${voucher.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
          {voucher.active ? 'Còn hiệu lực' : 'Hết hiệu lực'}
        </div>

        {/* Nút đóng */}
        <button
          className="absolute top-2 right-2 text-gray-500 hover:text-black text-xl"
          onClick={onClose}
        >
          &times;
        </button>

        <h2 className="text-xl font-bold mb-4 mt-6">Thông tin Voucher</h2>

        <div className="space-y-2">
          <p><strong>Mã:</strong> {voucher.code}</p>
          <p><strong>Mô tả:</strong> {voucher.description}</p>
          <p><strong>Giá trị:</strong> {voucher.value} {voucher.type === 'PERCENTAGE' ? '%' : 'VNĐ'}</p>
          {voucher.startTime && (
            <p><strong>Thời gian bắt đầu:</strong> {voucher.startTime}</p>
          )}
          {voucher.endTime && (
            <p><strong>Thời gian hết hạn:</strong> {voucher.endTime}</p>
          )}
        </div>
      </div>
    </div>
  );

}