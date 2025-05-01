import { useState } from "react";

type SidebarProps = {
  setSelectedMenu: (menu: string) => void;
};

const Sidebar = ({ setSelectedMenu }: SidebarProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="w-64 bg-gray-800 text-white h-full p-6">
      <h2 className="text-2xl font-semibold mb-6">Danh Mục Quản Lý</h2>
      <ul>
        <li>
          <button
            onClick={() => setSelectedMenu("profile")}
            className="w-full text-left py-2 px-4 hover:bg-gray-700 rounded"
          >
            Thông tin nhà hàng
          </button>
        </li>
        <li>
          <button
            onClick={() => setSelectedMenu("orders")}
            className="w-full text-left py-2 px-4 hover:bg-gray-700 rounded"
          >
            Đơn Hàng
          </button>
        </li>
        <li>
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="w-full text-left py-2 px-4 hover:bg-gray-700 rounded flex justify-between items-center"
          >
            Menu
            <span>{menuOpen ? "▲" : "▼"}</span>
          </button>
          {menuOpen && (
            <ul className="ml-4 mt-2">
              <li>
                <button
                  onClick={() => setSelectedMenu("main")}
                  className="w-full text-left py-1 px-4 hover:bg-gray-700 rounded"
                >
                  Món chính
                </button>
              </li>
              <li>
                <button
                  onClick={() => setSelectedMenu("side")}
                  className="w-full text-left py-1 px-4 hover:bg-gray-700 rounded"
                >
                  Món phụ
                </button>
              </li>
              <li>
                <button
                  onClick={() => setSelectedMenu("combo")}
                  className="w-full text-left py-1 px-4 hover:bg-gray-700 rounded"
                >
                  Chính + phụ
                </button>
              </li>
            </ul>
          )}
        </li>
        <li>
          <button
            onClick={() => setSelectedMenu("employees")}
            className="w-full text-left py-2 px-4 hover:bg-gray-700 rounded"
          >
            Nhân Viên
          </button>
        </li>
      </ul>
    </div>
  );
};

export default Sidebar;
