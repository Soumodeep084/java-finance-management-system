<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="https://cdn.tailwindcss.com"></script>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <title>Wealthory - Login</title>
        <style>
            body {
                background: linear-gradient(120deg, #f0f4f8 0%, #d9e7fa 100%);
            }
        </style>
    </head>
    <body>
        <!-- Navbar -->
        <jsp:include page="components/navbar.jsp" />
        <div class="min-h-screen flex items-center justify-center bg-gradient-to-tr from-slate-100 via-blue-50 to-teal-100">
            <div class="flex flex-col md:flex-row bg-white/90 rounded-2xl shadow-2xl overflow-hidden max-w-3xl w-full mx-3">
                <!-- Left Section (Brand/Illustration) -->
                <div class="hidden md:flex md:w-1/2 bg-gradient-to-br from-teal-700 via-blue-600 to-indigo-600 flex-col items-center justify-center text-white p-8">

                    <img src="images/big-logo.png" alt="Logo" class="w-28 mb-4 drop-shadow-lg">
                    <div class="text-2xl font-bold mb-2 tracking-wider">Finance Management</div>
                    <p class="text-md opacity-90">AI Powered Insights. Smarter Money Moves.</p>
                </div>
                <!-- Right Section (Form) -->
                <div class="w-full md:w-1/2 p-8 sm:p-10 flex flex-col justify-center">
                    <h2 class="text-3xl md:text-2xl font-bold text-blue-800 mb-6">Sign in to your account</h2>
                    <!-- Error Message -->
                    <c:if test="${not empty error}">
                        <div class="bg-rose-100 text-rose-700 p-3 rounded mb-4 border border-rose-300 animate-pulse">
                            ${error}
                        </div>
                    </c:if>
                    
                    <!-- Success Message -->
                    <c:if test="${not empty success}">
                        <div class="bg-emerald-100 text-blue-700 p-3 rounded mb-4 border border-emerald-300 animate-pulse">
                            ${success}
                        </div>
                    </c:if>
                    <form action="LoginServlet" method="post" class="space-y-4">
                        <div>
                            <label class="block text-gray-700 mb-1 font-medium">Username</label>
                            <input type="text" name="username"
                                   class="w-full p-3 border-2 border-blue-300 rounded-lg shadow focus:border-blue-600 focus:ring focus:ring-blue-200 outline-none transition"
                                   required>
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-1 font-medium">Password</label>
                            <input type="password" name="password"
                                   class="w-full p-3 border-2 border-blue-300 rounded-lg shadow focus:border-blue-600 focus:ring focus:ring-blue-200 outline-none transition"
                                   required>
                        </div>
                        <a href="forgot-password.jsp" class="text-red-500 my-3 font-semibold font-sans"> Forgot Password ? </a>
                        <button type="submit"
                                class="w-full py-2 md:py-3 bg-gradient-to-r from-blue-600 via-teal-600 to-indigo-600 text-white font-semibold rounded-lg shadow-lg hover:scale-105 hover:shadow-xl transition">
                            Login
                        </button>
                    </form>
                    <p class="text-gray-600 mt-6 text-sm text-center">
                        Don’t have an account?
                        <a href="signup.jsp" class="text-teal-700 hover:underline font-bold">Sign Up</a>
                    </p>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="components/footer.jsp" />
    </body>
</html>
