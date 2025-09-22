<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Forgot Password - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="pt-24 bg-gray-50">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-4 py-12 max-w-lg">
            <div class="bg-white rounded-2xl shadow p-8 font-sans">
                <h1 class="text-2xl font-bold text-teal-700 mb-2 text-center">Forgot Password</h1>
                <p class="text-gray-800 text-md mt-2 my-1">Reset password in 3 steps</p>
                <p class="text-gray-600 text-sm my-1">1. Entered Registered Email or Username</p>
                <p class="text-gray-600 text-sm my-1">2. Enter the OTP from the Requested Email</p>
                <p class="text-gray-600 text-sm my-1">3. Set a New Password</p>

                <c:if test="${not empty error}">
                    <div class="bg-rose-100 text-rose-700 px-4 py-3 rounded mb-4">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="bg-green-100 text-green-700 px-4 py-3 rounded mb-4">${success}</div>
                </c:if>

                <c:set var="phase" value="${empty phase ? 'request' : phase}"/>

                <!-- Phase 1: request OTP -->
                <c:if test="${phase == 'request'}">
                    <form action="${pageContext.request.contextPath}/ForgotPassword" method="post" class="space-y-5">
                        <input type="hidden" name="action" value="request"/>
                        <div>
                            <label for="identity" class="block text-sm text-gray-700 mb-1">Email or Username</label>
                            <input id="identity" name="identity" type="text"
                                   class="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-teal-300 focus:border-teal-500"
                                   placeholder="you@example.com or username" required/>
                        </div>
                        <button class="w-full bg-teal-700 text-white py-2.5 rounded-lg hover:bg-teal-800 transition">Send OTP</button>
                    </form>
                </c:if>

                <!-- Phase 2: verify OTP and reset password -->
                <c:if test="${phase == 'verify'}">
                    <form action="${pageContext.request.contextPath}/ForgotPassword" method="post" class="space-y-5">
                        <input type="hidden" name="action" value="reset"/>
                        <div>
                            <label for="otp" class="block text-sm text-gray-700 mb-1">Enter OTP</label>
                            <input id="otp" name="otp" type="number"
                                   class="w-full border rounded-lg px-3 py-2 tracking-widest focus:ring-2 focus:ring-teal-300 focus:border-teal-500"
                                   placeholder="6-digit OTP" required/>
                        </div>
                        <div>
                            <label for="newPassword" class="block text-sm text-gray-700 mb-1">New password</label>
                            <input id="newPassword" name="newPassword" type="password"
                                   class="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-teal-300 focus:border-teal-500"
                                   placeholder="At least 8 chars, use letters, numbers, symbols" required/>
                        </div>
                        <div>
                            <label for="confirmPassword" class="block text-sm text-gray-700 mb-1">Confirm new password</label>
                            <input id="confirmPassword" name="confirmPassword" type="password"
                                   class="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-teal-300 focus:border-teal-500"
                                   placeholder="Re-enter new password" required/>
                        </div>
                        <button class="w-full bg-teal-700 text-white py-2.5 rounded-lg hover:bg-teal-800 transition">Reset Password</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/ForgotPassword" method="post" class="mt-4">
                        <input type="hidden" name="action" value="resend"/>
                        <button class="text-sm text-teal-700 hover:underline" type="submit">Resend OTP</button>
                    </form>
                </c:if>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
