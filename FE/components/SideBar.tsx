type SidebarProps = {
    setSelectedMenu: (menu: string) => void;
  };
  
  const Sidebar = ({ setSelectedMenu }: SidebarProps) => {
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
              onClick={() => setSelectedMenu("menu")}
              className="w-full text-left py-2 px-4 hover:bg-gray-700 rounded"
            >
              Menu
            </button>
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
  