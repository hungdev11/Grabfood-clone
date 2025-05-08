"use client";

import { useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";

export default function ResetPasswordPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [tokenValid, setTokenValid] = useState<boolean | null>(null);

  useEffect(() => {
    const tokenParam = searchParams.get("token");
    if (!tokenParam) {
      setTokenValid(false);
      toast.error("Token không hợp lệ. Vui lòng yêu cầu liên kết đặt lại mật khẩu mới.");
      return;
    }

    setToken(tokenParam);
    
    // Validate token
    const validateToken = async () => {
      try {
        const response = await fetch(`http://localhost:6969/grab/auth/validate-reset-token?token=${tokenParam}`);
        if (!response.ok) {
          throw new Error("Invalid token");
        }
        setTokenValid(true);
      } catch (error) {
        console.error("Token validation error:", error);
        setTokenValid(false);
        toast.error("Token đã hết hạn. Vui lòng yêu cầu liên kết đặt lại mật khẩu mới.");
      }
    };

    validateToken();
  }, [searchParams]);

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (newPassword !== confirmPassword) {
      toast.error("Mật khẩu không khớp");
      return;
    }

    if (newPassword.length < 3) {
      toast.error("Mật khẩu phải có ít nhất 3 ký tự");
      return;
    }

    setIsLoading(true);

    try {
      const response = await fetch("http://localhost:6969/grab/auth/reset-password", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ 
          token,
          newPassword,
          confirmPassword
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || "Failed to reset password");
      }

      toast.success("Đặt lại mật khẩu thành công");
      
      // Redirect to login page after a short delay
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } catch (error) {
      console.error("Error resetting password:", error);
      toast.error("Không thể đặt lại mật khẩu. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  if (tokenValid === null) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p>Đang xác thực token...</p>
      </div>
    );
  }

  if (tokenValid === false) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Card className="w-[400px]">
          <CardHeader>
            <CardTitle>Link không hợp lệ</CardTitle>
            <CardDescription>
              Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button 
              className="w-full bg-green-600 hover:bg-green-700" 
              onClick={() => router.push("/login")}
            >
              Quay lại trang đăng nhập
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center">
      <Card className="w-[400px]">
        <CardHeader>
          <CardTitle>Đặt lại mật khẩu</CardTitle>
          <CardDescription>
            Tạo mật khẩu mới cho tài khoản của bạn
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleResetPassword} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="new-password">Mật khẩu mới</Label>
              <Input
                id="new-password"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                minLength={3}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirm-password">Xác nhận mật khẩu</Label>
              <Input
                id="confirm-password"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                minLength={3}
                required
              />
            </div>
            <Button 
              type="submit" 
              className="w-full bg-green-600 hover:bg-green-700"
              disabled={isLoading}
            >
              {isLoading ? "Đang cập nhật..." : "Đặt lại mật khẩu"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}