<%-- 
    Document   : index
    Created on : 20-Sept-2025, 8:31:31 am
    Author     : S DUTTA
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Wealthory - FMS</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-white min-h-screen">

        <!-- Navbar -->
        <jsp:include page="components/navbar.jsp" />

        <!-- Hero Section -->
        <section class="pt-40 pb-20 px-4 text-center">
            <div class="container mx-auto">
                <h1 class="text-3xl md:text-6xl pb-6 font-bold text-teal-700">
                    Manage Your Everyday Finances <br/> with Intelligence
                </h1>
                <p class="text-sm text-gray-600 mb-8 md:text-xl">
                    Smarter money management with AI <br/> track, analyze, and optimize your finances in real time.
                </p>
                <div class="flex justify-center space-x-4">
                    <a href="dashboard.jsp">
                        <button class="px-8 py-2 bg-teal-700 text-white rounded-lg hover:bg-teal-800">
                            Get Started
                        </button>
                    </a>
                </div>
                <div class="mt-5 md:mt-3">
                    <img src="images/hero-banner.jpeg" alt="Dashboard Preview" 
                         class="rounded-lg shadow-2xl border mx-auto" />
                </div>
            </div>
        </section>

        <!-- Stats Section -->
        <section id="stats" class="py-10 bg-blue-50 m-5 font-sans">
            <div class="mx-auto px-4">
                <h2 class="text-3xl font-bold text-center mb-12">Here our Stats</h2>
                <div class="grid grid-cols-2 md:grid-cols-4 gap-6 md:gap-8 text-center">
                    <div class="p-4 bg-white rounded shadow hover:bg-gray-100">
                        <div class="text-xl md:text-4xl font-bold text-blue-600 mb-2">75K+</div>
                        <div class="text-gray-600 text-sm md:text-xl">Users Onboarded</div>
                    </div>
                    <div class="p-4 bg-white rounded shadow hover:bg-gray-100">
                        <div class="text-xl md:text-4xl font-bold text-blue-600 mb-2">$3.1B+</div>
                        <div class="text-gray-600 text-sm md:text-xl">Financial Activity Tracked</div>
                    </div>
                    <div class="p-4 bg-white rounded shadow hover:bg-gray-100">
                        <div class="text-xl md:text-4xl font-bold text-blue-600 mb-2">99.99%</div>
                        <div class="text-gray-600 text-sm md:text-xl">System Availability</div>
                    </div>
                    <div class="p-4 bg-white rounded shadow hover:bg-gray-100">
                        <div class="text-xl md:text-4xl font-bold text-blue-600 mb-2">4.8/5</div>
                        <div class="text-gray-600 text-sm md:text-xl">Customer Satisfaction</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Features Section -->
        <section id="features" class="py-16 px-4 bg-gradient-to-br from-blue-50 via-cyan-50 to-indigo-100">
            <div class="container mx-auto max-w-7xl">
                <h2 class="text-4xl font-extrabold text-center mb-12 text-transparent bg-clip-text bg-gradient-to-r from-teal-700 via-blue-700 to-indigo-700">
                    Everything you need to manage your finances
                </h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-10">
                    <!-- Feature Card -->
                    <!-- Income tracking -->
                    <div class="p-8 bg-white/80 backdrop-blur-lg rounded-xl shadow-2xl hover:scale-105 transition text-center border-t-4 border-teal-400">
                        <div class="mx-auto mb-5 w-16 h-16 flex items-center justify-center rounded-full bg-gradient-to-tr from-teal-300 via-blue-400 to-indigo-200 shadow-inner text-4xl">
                            💼
                        </div>
                        <h3 class="text-xl font-bold text-teal-700 mb-3">Income Tracking</h3>
                        <p class="text-gray-600">Record salaries and deposits by account with back‑dated support and clean history views.</p>
                    </div>

                    <!-- Expense logging -->
                    <div class="p-8 bg-white/80 backdrop-blur-lg rounded-xl shadow-2xl hover:scale-105 transition text-center border-t-4 border-blue-400">
                        <div class="mx-auto mb-5 w-16 h-16 flex items-center justify-center rounded-full bg-gradient-to-tr from-indigo-200 via-blue-300 to-teal-300 shadow-inner text-4xl">
                            🧾
                        </div>
                        <h3 class="text-xl font-bold text-blue-700 mb-3">Expense Logging</h3>
                        <p class="text-gray-600">Add purchases with category and notes, with balance checks as of the selected date.</p>
                    </div>

                    <!-- Budgets -->
                    <div class="p-8 bg-white/80 backdrop-blur-lg rounded-xl shadow-2xl hover:scale-105 transition text-center border-t-4 border-indigo-400">
                        <div class="mx-auto mb-5 w-16 h-16 flex items-center justify-center rounded-full bg-gradient-to-tr from-indigo-200 via-violet-300 to-blue-300 shadow-inner text-4xl">
                            🥧
                        </div>
                        <h3 class="text-xl font-bold text-indigo-700 mb-3">Simple Budgets</h3>
                        <p class="text-gray-600">Set monthly limits by category and compare against actual spending.</p>
                    </div>

                    <!-- Charts -->
                    <div class="p-8 bg-white/80 backdrop-blur-lg rounded-xl shadow-2xl hover:scale-105 transition text-center border-t-4 border-teal-400">
                        <div class="mx-auto mb-5 w-16 h-16 flex items-center justify-center rounded-full bg-gradient-to-tr from-teal-200 via-blue-300 to-indigo-300 shadow-inner text-4xl">
                            📊
                        </div>
                        <h3 class="text-xl font-bold text-teal-700 mb-3">Clean Charts</h3>
                        <p class="text-gray-600">View last 6 months trends and 90‑day category breakdowns for income and expenses.</p>
                    </div>

                    <!-- Accounts -->
                    <div class="p-8 bg-white/80 backdrop-blur-lg rounded-xl shadow-2xl hover:scale-105 transition text-center border-t-4 border-blue-400">
                        <div class="mx-auto mb-5 w-16 h-16 flex items-center justify-center rounded-full bg-gradient-to-tr from-blue-200 via-teal-300 to-indigo-300 shadow-inner text-4xl">
                            💳
                        </div>
                        <h3 class="text-xl font-bold text-blue-700 mb-3">Accounts & Balance</h3>
                        <p class="text-gray-600">Maintain account balances and liabilities with automatic updates on entries.</p>
                    </div>

                    <!-- Email & OTP -->
                    <div class="p-8 bg-white/80 backdrop-blur-lg rounded-xl shadow-2xl hover:scale-105 transition text-center border-t-4 border-indigo-400">
                        <div class="mx-auto mb-5 w-16 h-16 flex items-center justify-center rounded-full bg-gradient-to-tr from-indigo-200 via-blue-300 to-teal-300 shadow-inner text-4xl">
                            ✉️
                        </div>
                        <h3 class="text-xl font-bold text-indigo-700 mb-3">Email & OTP</h3>
                        <p class="text-gray-600">Signup welcomes and secure delete OTPs via integrated mail service.</p>
                    </div>

                </div>
            </div>
        </section>

        <!-- Footer -->
        <jsp:include page="components/footer.jsp" />

    </body>
</html>

