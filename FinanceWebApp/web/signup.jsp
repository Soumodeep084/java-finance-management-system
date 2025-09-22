<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Wealthory - Sign Up</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <script src="https://cdn.tailwindcss.com"></script>
        <style>
            body {
                background: linear-gradient(135deg, #e0f2fe 0%, #d9f99d 100%);
            }
        </style>
    </head>
    <body class="min-h-screen flex flex-col">
        <!-- Navbar -->
        <jsp:include page="components/navbar.jsp" />

        <main class="flex-grow flex items-center justify-center px-4 pt-24 pb-5">
            <div class="max-w-md w-full bg-white/80 backdrop-blur-md rounded-3xl shadow-2xl p-10">
                <h1 class="text-center text-3xl font-extrabold text-teal-700 mb-8 tracking-wide">Finance Management</h1>

                <c:if test="${not empty error}">
                    <div class="bg-rose-100 text-rose-700 px-4 py-3 rounded-lg mb-6 border border-rose-300 shadow animate-pulse">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="bg-green-100 text-green-700 px-4 py-3 rounded-lg mb-6 border border-green-300 shadow">
                        ${success}
                    </div>
                </c:if>
                <c:if test="${param.msg == 'account_deleted'}">
                    <div class="bg-green-100 text-green-700 px-4 py-3 rounded mb-4">
                        Account deleted successfully.
                    </div>
                </c:if>

                <form action="SignUpServlet" method="post" class="space-y-4">
                    <div>
                        <label for="name" class="block text-gray-700 font-semibold mb-1">Full Name</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:border-teal-500 focus:ring-2 focus:ring-teal-300 outline-none transition"
                            placeholder="Your full name"
                            required
                            />
                    </div>
                    <div>
                        <label for="username" class="block text-gray-700 font-semibold mb-1">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:border-teal-500 focus:ring-2 focus:ring-teal-300 outline-none transition"
                            placeholder="Enter your email"
                            required
                            />
                    </div>
                    <div>
                        <label for="username" class="block text-gray-700 font-semibold mb-1">Username</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:border-teal-500 focus:ring-2 focus:ring-teal-300 outline-none transition"
                            placeholder="Choose a username"
                            required
                            />
                    </div>
                    <div>
                        <label for="password" class="block text-gray-700 font-semibold mb-1">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:border-teal-500 focus:ring-2 focus:ring-teal-300 outline-none transition"
                            placeholder="Create a strong password"
                            required
                            />
                    </div>

                    <div>
                        <label for="password" class="block text-gray-700 font-semibold mb-1">Re-Enter Password</label>
                        <input
                            type="password"
                            id="reenter-password"
                            name="reenter-password"
                            class="w-full px-3 py-2 rounded-xl border border-gray-300 focus:border-teal-500 focus:ring-2 focus:ring-teal-300 outline-none transition"
                            placeholder="Re-Enter your strong password"
                            required
                            />
                    </div>

                    <button
                        type="submit"
                        class="w-full bg-gradient-to-r from-teal-600 via-cyan-600 to-blue-600 text-white py-3 rounded-xl font-bold shadow-lg hover:scale-105 hover:shadow-xl transition"
                        >
                        Sign Up
                    </button>
                </form>

                <p class="text-center mt-6 text-gray-700">
                    Already have an account?
                    <a href="login.jsp" class="text-coral-600 font-semibold hover:underline text-teal-700">Login</a>
                </p>
            </div>
        </main>

        <!-- Footer -->
        <jsp:include page="components/footer.jsp" />
    </body>
</html>
