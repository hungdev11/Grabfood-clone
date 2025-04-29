"use client";

import { useState, useEffect } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { toast } from "sonner";
import { fetchWithAuth } from "@/utils/api";

interface UserData {
  name: string;
  email: string;
  phone: string;
}

export default function PersonalInformation() {
  const [userData, setUserData] = useState<UserData>({
    name: "",
    email: "",
    phone: "",
  });

  const [loading, setLoading] = useState(true);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  // Fetch user data when component mounts
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        setLoading(true);
        const userId = localStorage.getItem("grabUserId");
        if (!userId) {
          throw new Error("User ID not found");
        }
        // Using our authenticated fetch function
        const res = await fetchWithAuth(
          "http://localhost:6969/grab/auth/user/me"
        );

        if (!res.ok) throw new Error("Failed to fetch user data");
        const responseData = await res.json();
        console.log("Full API response:", responseData);
        console.log(
          "Response structure:",
          JSON.stringify(responseData, null, 2)
        );

        // Try different paths to find the user data
        let foundName = responseData.data?.name || responseData.name || "";
        let foundEmail = responseData.data?.email || responseData.email || "";
        let foundPhone = responseData.data?.phone || responseData.phone || "";

        console.log("Extracted data:", { foundName, foundEmail, foundPhone });

        // Set whatever we can find
        setUserData({
          name: foundName,
          email: foundEmail,
          phone: foundPhone,
        });
        console.log("UserData state after setting:", {
          name: foundName,
          email: foundEmail,
          phone: foundPhone,
        });
      } catch (error) {
        console.error("Error fetching user data:", error);
        toast.error("Không thể tải thông tin người dùng");
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  const handleInfoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserData({ ...userData, [e.target.name]: e.target.value });
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPasswordData({ ...passwordData, [e.target.name]: e.target.value });
  };

  // const handleInfoSubmit = async (e: React.FormEvent) => {
  //   e.preventDefault();
  //   try {
  //     const res = await fetchWithAuth(
  //       "http://localhost:6969/grab/user/update-profile",
  //       {
  //         method: "PUT",
  //         body: JSON.stringify(userData),
  //       }
  //     );

  //     if (!res.ok) throw new Error("Failed to update user info");

  //     toast.success("Cập nhật thông tin thành công");
  //   } catch (error) {
  //     console.error("Error updating user info:", error);
  //     toast.error("Không thể cập nhật thông tin");
  //   }
  // };

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      toast.error("Mật khẩu mới không khớp");
      return;
    }

    try {
      const userId = localStorage.getItem("grabUserId");
      if (!userId) {
        throw new Error("User ID not found");
      }
      const res = await fetchWithAuth(
        "http://localhost:6969/grab/auth/user/change-password",
        {
          method: "PUT",
          body: JSON.stringify({
            currentPassword: passwordData.currentPassword,
            newPassword: passwordData.newPassword,
            confirmPassword: passwordData.confirmPassword,
          }),
        }
      );

      if (!res.ok) {
        // First check response content type
        const contentType = res.headers.get("content-type");
        console.log("Response content type:", contentType);

        let errorData = null;

        // Try to get response text first
        const responseText = await res.text();
        console.log("Raw response text:", responseText);

        // Then try to parse it as JSON if it looks like JSON
        if (
          responseText &&
          (responseText.startsWith("{") || responseText.startsWith("["))
        ) {
          try {
            errorData = JSON.parse(responseText);
            console.log("Parsed error data:", errorData);
          } catch (e) {
            console.error("Failed to parse error response as JSON:", e);
          }
        }

        // Handle specific error cases
        if (
          errorData?.code === "INVALID_PASSWORD" ||
          errorData?.errorCode === "INVALID_PASSWORD"
        ) {
          toast.error("Mật khẩu hiện tại không đúng", {
            duration: 4000,
            position: "top-center",
          });
        } else if (responseText?.includes("INVALID_PASSWORD")) {
          // Fallback check for plain text responses
          toast.error("Mật khẩu hiện tại không đúng", {
            duration: 4000,
            position: "top-center",
          });
        } else if (errorData?.message) {
          toast.error(errorData.message);
        } else if (res.status === 401 || res.status === 403) {
          toast.error("Mật khẩu hiện tại không đúng");
        } else {
          toast.error(
            `Không thể đổi mật khẩu (${res.status}): ${responseText.substring(
              0,
              100
            )}`
          );
        }
        return;
      }
      console.log(
        "PASSWORD CHANGE SUCCESS! This should be visible in the console"
      );
      toast.success("Đổi mật khẩu thành công!", {
        duration: 4000, // Show it longer
        position: "top-center", // More visible position
      });
      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
    } catch (error) {
      console.error("Error changing password:", error);
      toast.error("Không thể đổi mật khẩu");
    }
  };

  if (loading) {
    return <div className="text-center py-8">Đang tải thông tin...</div>;
  }

  return (
    <div className="space-y-6">
      <Tabs defaultValue="info" className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="info">Thông tin cá nhân</TabsTrigger>
          <TabsTrigger value="password">Đổi mật khẩu</TabsTrigger>
        </TabsList>

        <TabsContent value="info">
          <Card>
            <CardContent className="pt-6 space-y-4">
              <div className="space-y-2">
                <Label htmlFor="name" className="text-sm text-gray-600">
                  Họ và tên
                </Label>
                <div className="p-2 bg-gray-50 border rounded-md">
                  <p className="text-gray-800">
                    {userData.name || "Chưa cập nhật"}
                  </p>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="email" className="text-sm text-gray-600">
                  Email
                </Label>
                <div className="p-2 bg-gray-50 border rounded-md">
                  <p className="text-gray-800">
                    {userData.email || "Chưa cập nhật"}
                  </p>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="phone" className="text-sm text-gray-600">
                  Số điện thoại
                </Label>
                <div className="p-2 bg-gray-50 border rounded-md">
                  <p className="text-gray-800">
                    {userData.phone || "Chưa cập nhật"}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="password">
          <Card>
            <CardContent className="pt-6">
              <form onSubmit={handlePasswordSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="currentPassword">Mật khẩu hiện tại</Label>
                  <Input
                    id="currentPassword"
                    name="currentPassword"
                    type="password"
                    value={passwordData.currentPassword}
                    onChange={handlePasswordChange}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="newPassword">Mật khẩu mới</Label>
                  <Input
                    id="newPassword"
                    name="newPassword"
                    type="password"
                    value={passwordData.newPassword}
                    onChange={handlePasswordChange}
                    required
                    minLength={3}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirmPassword">Xác nhận mật khẩu mới</Label>
                  <Input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    value={passwordData.confirmPassword}
                    onChange={handlePasswordChange}
                    required
                    minLength={3}
                  />
                </div>

                <Button
                  type="submit"
                  className="bg-green-600 hover:bg-green-700"
                >
                  Đổi mật khẩu
                </Button>
              </form>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
