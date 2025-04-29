"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast, Toaster } from "sonner";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

interface ForgotPasswordProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function ForgotPassword({
  isOpen,
  onClose,
}: ForgotPasswordProps) {
  const [email, setEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleRequestReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await fetch(
        "http://localhost:6969/grab/auth/forgot-password",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ email }),
        }
      );

      // Get response text first to analyze it
      const responseText = await response.text();

      // Try to parse as JSON if possible
      let errorData = null;
      try {
        if (responseText && responseText.trim().length > 0) {
          errorData = JSON.parse(responseText);
          console.log("Successfully parsed error data:", errorData);
        }
      } catch (e) {
        console.error("Failed to parse response as JSON:", e);
      }

      if (!response.ok) {
        console.log("Response status:", response.status);
        console.log("Response text:", responseText);

        // Check if the response text directly contains our error message without attempting JSON parsing
        if (
          responseText.includes("GOOGLE_ACCOUNT_NO_PASSWORD") ||
          responseText.includes("Tài khoản này đăng nhập bằng Google")
        ) {
          toast.error(
            "Tài khoản này đăng nhập bằng Google. Vui lòng sử dụng nút 'Đăng nhập với Google'."
          );
          return;
        }

        // If we have valid error data from JSON parsing
        if (errorData) {
          if (
            errorData.code === "GOOGLE_ACCOUNT_NO_PASSWORD" ||
            errorData.errorCode === "GOOGLE_ACCOUNT_NO_PASSWORD"
          ) {
            toast.error(
              "Tài khoản này đăng nhập bằng Google. Vui lòng sử dụng nút 'Đăng nhập với Google'."
            );
            return;
          }

          if (errorData.message) {
            toast.error(errorData.message);
            return;
          }
        }

        // Fall back to a generic error
        toast.error(
          "Không thể gửi liên kết đặt lại mật khẩu. Vui lòng thử lại."
        );
        return;
      }

      toast.success("Đã gửi liên kết đặt lại mật khẩu đến email của bạn");
      setEmail("");
      onClose();
    } catch (error) {
      console.error("Error requesting password reset:", error);
      toast.error("Không thể gửi liên kết đặt lại mật khẩu. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Quên mật khẩu</DialogTitle>
            <DialogDescription>
              Nhập email của bạn để nhận liên kết đặt lại mật khẩu
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleRequestReset} className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="your@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                disabled={isLoading}
              >
                Hủy
              </Button>
              <Button
                type="submit"
                className="bg-green-600 hover:bg-green-700"
                disabled={isLoading}
              >
                {isLoading ? "Đang gửi..." : "Gửi liên kết"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
      <Toaster position="top-center" richColors />
    </>
  );
}
