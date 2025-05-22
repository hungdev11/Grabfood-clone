"use client";

import RegisterRestaurantForm from "@/components/RegisterRestaurantForm";
import React, { useState, useEffect } from "react";
import ForgotPassword from "@/components/ForgotPassword";
import { useRouter } from "next/navigation";
import Image from "next/image";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import Header from "@/components/header";
import Footer from "@/components/footer";
import Link from "next/link";
import { handleLoginSuccess } from "@/utils/authService";

export default function LoginPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [forgotPasswordOpen, setForgotPasswordOpen] = useState(false);
  const [showRegisterRes, setShowRegisterRes] = useState(false);

  // Check for token when component mounts (for OAuth redirect)
  useEffect(() => {
    const processOAuthToken = async () => {
      try {
        console.log("Full URL:", window.location.href);
        const queryParams = new URLSearchParams(window.location.search);
        let token = queryParams.get("token");

        console.log("Token from query params:", token);

        const fragment = window.location.hash.substring(1);
        console.log("URL fragment:", fragment);

        // Combine token and fragment to get the full token
        if (token && fragment) {
          token = token + "#" + fragment;
          console.log("Combined token:", token);
        }

        if (token) {
          // Process the token using our handleLoginSuccess function
          await handleLoginSuccess(token);

          // Clean URL
          window.history.replaceState(
            {},
            document.title,
            window.location.pathname
          );

          // Notify user
          alert("Google login successful!");
        }
      } catch (error) {
        console.error("Error processing OAuth redirect:", error);
        alert("Something went wrong with the login process. Please try again.");
      }
    };

    processOAuthToken();
  }, []);

  // Login form state
  const [loginData, setLoginData] = useState({
    username: "", // Changed from email to username for flexibility
    password: "",
  });

  // Register form state
  const [registerData, setRegisterData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });

  // Handle login form changes
  const handleLoginChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({
      ...loginData,
      [e.target.name]: e.target.value,
    });
  };

  // Handle register form changes
  const handleRegisterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setRegisterData({
      ...registerData,
      [e.target.name]: e.target.value,
    });
  };
  // Handle Google OAuth login
  const handleGoogleLogin = () => {
    console.log("Redirecting to Google OAuth...");
    window.location.href =
      "http://localhost:6969/grab/oauth2/authorization/google";
  };
  // Handle login submission
  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const requestBody = {
        username: loginData.username,
        password: loginData.password,
      };
      console.log("Sending auth request:", requestBody);
      const response = await fetch(
        "http://localhost:6969/grab/auth/generateToken",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        }
      );
      

      const responseText = await response.text();

      if (!response.ok) {
        alert(
          responseText ||
            "Login failed. Please check your account and try again."
        );
        setIsLoading(false);
        return;
      }

      // Kiểm tra responseText có đúng định dạng token không (phải có dấu #)
      if (!responseText.includes("#")) {
        alert(
            "Login failed. Please check your account and try again."
        );
        setIsLoading(false);
        return;
      }

      // Use our new function to handle login success
      await handleLoginSuccess(responseText);

      // Success message
      alert("Login successful!");
    } catch (error: any) {
      console.error("Login error:", error);
      alert("Login failed. Please check your account and try again.");
    } finally {
      setIsLoading(false);
    }
  };

  // Handle register submission
  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    if (registerData.password !== registerData.confirmPassword) {
      alert("Passwords don't match");
      setIsLoading(false);
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:6969/grab/auth/addNewAccount",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: registerData.name,
            email: registerData.email,
            password: registerData.password,
            phone: registerData.phone,
          }),
        }
      );

      // Get text response instead of trying to parse JSON
      const responseText = await response.text();

      if (!response.ok) {
        throw new Error(responseText || "Registration failed");
      }

      // Check if registration was successful based on response text
      if (
        responseText.includes("Account Added") ||
        responseText.includes("Success")
      ) {
        alert("Registration successful! Please log in.");
        // Switch to login tab
        document.getElementById("login-tab")?.click();
      } else {
        // If we got an OK response but unexpected message
        console.warn("Unexpected response:", responseText);
        alert("Registration completed. Please log in.");
        document.getElementById("login-tab")?.click();
      }
    } catch (error: any) {
      console.error("Registration error:", error);
      alert("Registration failed. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen flex-col">
      {/* <Header /> */}

      <main className="flex-1 flex items-center justify-center p-6 bg-gray-50">
        <div className="w-full max-w-md">
          <Card className="shadow-md">
            <CardHeader className="text-center">
              <Link href="/" className="mb-4 flex justify-center">
                <div className="text-[#00B14F] font-bold text-2xl">
                  GrabFood
                </div>
              </Link>
              <CardTitle className="text-2xl font-bold">Welcome</CardTitle>
              <CardDescription>
                Sign in to your account or create a new one
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Tabs defaultValue="login" className="w-full">
                <TabsList className="grid w-full grid-cols-2 mb-6">
                  <TabsTrigger id="login-tab" value="login">
                    Login
                  </TabsTrigger>
                  <TabsTrigger value="register">Register</TabsTrigger>
                </TabsList>

                <TabsContent value="login">
                  <form onSubmit={handleLoginSubmit} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="username">Email or Phone Number</Label>
                      <Input
                        id="username"
                        name="username"
                        type="text"
                        placeholder="Email or Phone Number"
                        value={loginData.username}
                        onChange={handleLoginChange}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <div className="flex items-center justify-between">
                        <Label htmlFor="password">Mật khẩu</Label>
                        <button
                          type="button"
                          className="text-sm text-green-600 hover:underline"
                          onClick={() => setForgotPasswordOpen(true)}
                        >
                          Quên mật khẩu?
                        </button>
                      </div>
                      <Input
                        id="password"
                        name="password"
                        type="password"
                        placeholder="••••••••"
                        value={loginData.password}
                        onChange={handleLoginChange}
                        required
                      />
                    </div>
                    <Button
                      type="submit"
                      className="w-full bg-[#00B14F] hover:bg-[#00A040] text-white"
                      disabled={isLoading}
                    >
                      {isLoading ? "Logging in..." : "Log in"}
                    </Button>
                  </form>
                </TabsContent>

                <TabsContent value="register">
                  <form onSubmit={handleRegisterSubmit} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="name">Full Name</Label>
                      <Input
                        id="name"
                        name="name"
                        placeholder="John Doe"
                        value={registerData.name}
                        onChange={handleRegisterChange}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="registerEmail">Email</Label>
                      <Input
                        id="registerEmail"
                        name="email"
                        type="email"
                        placeholder="your@email.com"
                        value={registerData.email}
                        onChange={handleRegisterChange}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="phone">Phone Number</Label>
                      <Input
                        id="phone"
                        name="phone"
                        type="tel"
                        placeholder="0901234567"
                        value={registerData.phone}
                        onChange={handleRegisterChange}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="registerPassword">Password</Label>
                      <Input
                        id="registerPassword"
                        name="password"
                        type="password"
                        placeholder="••••••••"
                        value={registerData.password}
                        onChange={handleRegisterChange}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="confirmPassword">Confirm Password</Label>
                      <Input
                        id="confirmPassword"
                        name="confirmPassword"
                        type="password"
                        placeholder="••••••••"
                        value={registerData.confirmPassword}
                        onChange={handleRegisterChange}
                        required
                      />
                    </div>
                    <Button
                      type="submit"
                      className="w-full bg-[#00B14F] hover:bg-[#00A040] text-white"
                      disabled={isLoading}
                    >
                      {isLoading ? "Creating account..." : "Create account"}
                    </Button>
                  </form>
                </TabsContent>
              </Tabs>

              <div className="mt-6">
                <div className="relative">
                  <div className="absolute inset-0 flex items-center">
                    <span className="w-full border-t"></span>
                  </div>
                  <div className="relative flex justify-center text-xs uppercase">
                    <span className="bg-white px-2 text-gray-500">
                      Or continue with
                    </span>
                  </div>
                </div>

                <div className="mt-4 flex justify-center">
                  <Button
                    variant="outline"
                    type="button"
                    className="w-1/2"
                    onClick={handleGoogleLogin}
                  >
                    <svg
                      className="mr-2 h-4 w-4"
                      fill="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path d="M20.283 10.356h-8.327v3.451h4.792c-.446 2.193-2.313 3.453-4.792 3.453a5.27 5.27 0 0 1-5.279-5.28 5.27 5.27 0 0 1 5.279-5.279c1.259 0 2.397.447 3.29 1.178l2.6-2.599c-1.584-1.381-3.615-2.233-5.89-2.233a8.908 8.908 0 0 0-8.934 8.934 8.907 8.907 0 0 0 8.934 8.934c4.467 0 8.529-3.249 8.529-8.934 0-.528-.081-1.097-.202-1.625z"></path>
                    </svg>
                    Google
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
          <Button
            variant="outline"
            className="w-full mt-4"
            onClick={() => setShowRegisterRes(true)}
          >
            Đăng ký nhà hàng
          </Button>
          {showRegisterRes && (
            <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
              <div className="bg-white rounded-lg shadow-lg p-4 w-full max-w-lg max-h-[90vh] overflow-y-auto">
                <RegisterRestaurantForm
                  onClose={() => setShowRegisterRes(false)}
                />
              </div>
            </div>
          )}
        </div>
      </main>

      <Footer />
      <ForgotPassword
        isOpen={forgotPasswordOpen}
        onClose={() => setForgotPasswordOpen(false)}
      />
    </div>
  );
}
