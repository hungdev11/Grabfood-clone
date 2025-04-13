
'use client';

import Footer from "@/components/footer";
import Header from "@/components/header";
import Image from "next/image";
import { useState } from "react";
import { MapPin, Star, Clock, Heart, ShoppingCart, ShoppingBag } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
import Cart from "@/components/cart";
import ResListHome from "@/components/ResListIndex";
export default function Home() {
	const [isCartOpen, setIsCartOpen] = useState<boolean>(false);
	const [itemCount, setItemCount] = useState<number>(0);
	const [totalPrice, setTotalPrice] = useState<number>(0);
  
	// Hàm xử lý khi giỏ hàng thay đổi
	const handleCartChange = (count: number, price: number) => {
	  setItemCount(count);
	  setTotalPrice(price);
	};

	return (
		<div className="flex min-h-screen flex-col">
		{/* Header */}
		<Header />
			{/* Hero Banner */}
			<div className="relative h-[300px] w-full overflow-hidden">
				<Image
					src="/VN-new-4.jpg"
					alt="Food Banner"
					width={1200}
					height={300}
					className="h-full w-full object-cover"
					priority
				/>
				<div className="absolute inset-0 bg-gradient-to-r from-black/40 to-transparent">
					<div className="mx-auto max-w-7xl px-4">
						<div className="mt-16 max-w-md bg-white p-6 rounded-lg shadow-sm">
							<h2 className="text-sm text-gray-600">
								Good Evening
							</h2>
							<h1 className="mb-4 text-2xl font-bold">
								Where should we deliver your food today?
							</h1>
							<div className="relative mb-4">
								<Input
									className="h-10 w-full rounded-md border border-gray-300 pl-10 pr-4"
									placeholder="Nhập địa chỉ của bạn"
								/>
								<MapPin className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-red-500" />
							</div>
							<Button className="w-full bg-[#00B14F] hover:bg-[#00A040] text-white">
								Tìm kiếm
							</Button>
						</div>
					</div>
				</div>
			</div>

			{/* Deals Section */}
			<div className="mx-auto max-w-7xl px-4 py-8">
				<h2 className="mb-6 text-xl font-bold">
					Ưu đãi GrabFood tại{" "}
					<span className="text-[#00B14F]">Hà nội</span>
				</h2>

				<ResListHome />

				<div className="mt-4 rounded-md border border-gray-200 p-3 text-center text-sm text-gray-600">
					See all promotions
				</div>
			</div>

			{/* Food Categories */}
			<div className="mx-auto max-w-7xl px-4 py-8">
				<h2 className="mb-6 text-xl font-bold">
					There&apos;s something for everyone!
				</h2>
				<div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
					{foodCategories.map((category, index) => (
						<div
							key={index}
							className="cursor-pointer">
							<div className="mb-2 overflow-hidden rounded-lg">
								<Image
									src={category.image || "/placeholder.svg"}
									alt={category.name}
									width={200}
									height={200}
									className="h-32 w-full object-cover transition-transform hover:scale-105"
								/>
							</div>
							<p className="text-center text-sm font-medium">
								{category.name}
							</p>
						</div>
					))}
				</div>
			</div>

			{/* Why Order Section */}
			<div className="mx-auto max-w-7xl px-4 py-8">
				<h2 className="mb-6 text-xl font-bold">
					Vì sao bạn nên Order trên GrabFood?
				</h2>
				<div className="space-y-4">
					{whyOrderReasons.map((reason, index) => (
						<div
							key={index}
							className="flex items-start gap-2">
							<div className="mt-1 h-2 w-2 rounded-full bg-black"></div>
							<div>
								<span className="font-medium">
									{reason.title}:{" "}
								</span>
								<span className="text-sm text-gray-600">
									{reason.description}
								</span>
							</div>
						</div>
					))}
				</div>
			</div>

			{/* FAQ Section */}
			<div className="mx-auto max-w-7xl px-4 py-8">
				<h2 className="mb-6 text-xl font-bold">
					Những câu hỏi thường gặp
				</h2>
				<div className="rounded-lg border border-gray-200 p-4">
					<h3 className="mb-2 font-medium">GrabFood là gì?</h3>
					<p className="text-sm text-gray-600">
						Lorem, ipsum dolor sit amet consectetur adipisicing
						elit. Ipsum illo placeat quos unde cum a wide selection
						of merchant partners in Vietnam. GrabFood là dịch vụ đặt
						đồ ăn trực tuyến và giao hàng của Grab. Chúng tôi kết
						nối bạn với các nhà hàng yêu thích của bạn. Từ đồ ăn địa
						phương đến các món ăn quốc tế, từ đồ ăn nhanh đến các
						món ăn đặc sản, từ món chính đến món tráng miệng, chúng
						tôi đều có.
					</p>
					<Button
						variant="outline"
						className="mt-4 w-full">
						Read More
					</Button>
				</div>
			</div>

			{/* Features Section */}
			<div className="mx-auto max-w-7xl px-4 py-8">
				<div className="grid grid-cols-1 gap-8 md:grid-cols-2">
					<div className="flex flex-col items-center text-center">
						<Image
							src="/bottom-food-options.svg"
							alt="Curated restaurants"
							width={150}
							height={150}
							className="mb-4"
						/>
						<h3 className="mb-2 font-bold">Curated restaurants</h3>
						<p className="text-sm text-gray-600">
							From street bites to big meals, we won&apos;t limit
							your appetite. Go ahead and order all you want.
						</p>
					</div>
					<div className="flex flex-col items-center text-center">
						<Image
							src="/ilus-cool-features-app.svg"
							alt="App features"
							width={150}
							height={150}
							className="mb-4"
						/>
						<h3 className="mb-2 font-bold">
							More cool features available on the app
						</h3>
						<p className="text-sm text-gray-600">
							Download Grab app to use other payment methods and
							enjoy seamless communication with your driver.
						</p>
						<div className="mt-4 flex gap-4">
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
					</div>
				</div>
			</div>

			{/* Footer */}
			<Footer />
		</div>
	);
}

const foodCategories = [
	{ name: "Trà sữa", image: "/categories/tra-sua.webp" },
	{ name: "Đồ uống lạnh", image: "/categories/do-uong-lanh.webp" },
	{ name: "Cơm tấm", image: "/categories/com-tam.webp" },
	{ name: "Rau trộn", image: "/categories/rau-tron.webp" },
	{ name: "Bánh Mì", image: "/categories/banh-mi.webp" },
	{ name: "Pizza", image: "/categories/pizza.webp" },
	{ name: "Thịt gà", image: "/categories/thit-ga.webp" },
	{ name: "Cơm", image: "/categories/com.webp" },
	{ name: "Thức ăn nhanh", image: "/categories/thuc-an-nhanh.webp" },
	{ name: "Hiso Party", image: "/categories/hiso-party.webp" },
	{ name: "Weekend Treats", image: "/categories/weekend-treats.webp" },
	{ name: "Gà rán", image: "/categories/ga-ran.webp" },
	{ name: "Đồ ăn nhẹ", image: "/categories/do-an-nhe.webp" },
	{ name: "Cháo", image: "/categories/chao.webp" },
	{ name: "Mì Ý", image: "/categories/mi-y.webp" },
];

const whyOrderReasons = [
	{
		title: "Muốn nhanh",
		description:
			"GrabFood cung cấp dịch vụ giao đồ ăn nhanh nhất thị trường.",
	},
	{
		title: "Đồ ăn ngon mỗi ngày",
		description:
			"Từ món ăn địa phương đến các món ăn quốc tế hoặc chuỗi nhà hàng đồ ăn nổi tiếng, bạn có thể dễ dàng tìm thấy những món ăn ngon trên GrabFood.",
	},
	{
		title: "Không mất thêm phí",
		description:
			"Tự do lựa chọn các món ăn yêu thích. Thanh toán đúng giá hiển thị trên ứng dụng, không phí ẩn.",
	},
	{
		title: "Nhiều ưu đãi hấp dẫn",
		description:
			"Tích điểm GrabRewards mỗi khi đặt món, cơ hội để đổi lấy ưu đãi hấp dẫng, giảm giá đồ ăn, và nhiều ưu đãi hơn.",
	},
];
