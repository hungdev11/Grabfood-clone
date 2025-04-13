// components/Footer.tsx
import Image from "next/image";

export default function Footer() {
  return (
    <footer className="mt-auto bg-[#00B14F] py-8 text-white">
      <div className="mx-auto max-w-7xl px-4">
        <div className="mb-8 grid grid-cols-1 gap-8 md:grid-cols-3">
          <div>
            <Image
              src="/logo-grabfood-white2.svg"
              alt="GrabFood"
              width={120}
              height={30}
              className="mb-4"
            />
            <ul className="space-y-2 text-sm">
              <li>Về GrabFood</li>
              <li>Về Grab</li>
              <li>Blog</li>
            </ul>
          </div>
          <div>
            <h3 className="mb-4 font-bold">Mối quan tâm GrabFood</h3>
            <ul className="space-y-2 text-sm">
              <li>Trở thành tài xế Grab</li>
              <li>Câu hỏi thường gặp</li>
            </ul>
          </div>
          <div>
            <h3 className="mb-4 font-bold">Trung tâm hỗ trợ</h3>
            <div className="flex gap-4">
              <div className="h-6 w-6 rounded-full bg-white"></div>
              <div className="h-6 w-6 rounded-full bg-white"></div>
              <div className="h-6 w-6 rounded-full bg-white"></div>
            </div>
          </div>
        </div>
        <div className="flex justify-center gap-4">
          <Image
            src="/logo-appstore.svg"
            alt="App Store"
            width={120}
            height={40}
          />
          <Image
            src="/logo-playstore.svg"
            alt="Google Play"
            width={120}
            height={40}
          />
        </div>
        <div className="mt-8 text-center text-xs">
          © 2023 Grab. Các điều khoản áp dụng • Chính sách bảo mật
        </div>
      </div>
    </footer>
  );
}