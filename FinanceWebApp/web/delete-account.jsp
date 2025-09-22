<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Delete Account - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="pt-24 bg-gray-50">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-4 py-12 max-w-2xl">
            <div class="bg-white rounded-2xl shadow p-8">
                <h1 class="text-2xl font-bold text-rose-700 mb-4">Delete Account</h1>

                <div class="flex items-start gap-3 bg-rose-50 border border-rose-200 rounded p-4 mb-6">
                    <span class="text-rose-600 text-xl">⚠️</span>
                    <p class="text-rose-700">
                        Deleting this account is permanent and cannot be undone. All user data, accounts, incomes, expenses, budgets, and settings will be removed.
                    </p>
                </div>

                <c:if test="${not empty error}">
                    <div class="bg-rose-100 text-rose-700 px-4 py-3 rounded mb-4">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="bg-green-100 text-green-700 px-4 py-3 rounded mb-4">${success}</div>
                </c:if>

                <form action="DeleteAccountServlet" method="post" class="space-y-5">
                    <div>
                        <label for="confirmText" class="block text-sm text-gray-700 mb-1">
                            Type “Delete Account” to confirm
                        </label>
                        <input id="confirmText" name="confirmText" type="text"
                               class="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-rose-200 focus:border-rose-400"
                               placeholder='Delete Account' required/>
                        <p class="text-xs text-gray-500 mt-1">This confirmation prevents accidental deletions.</p>
                    </div>

                    <div>
                        <label for="otp" class="block text-sm text-gray-700 mb-1">
                            Enter OTP sent to registered email
                        </label>
                        <input id="otp" name="otp" type="text"
                               class="w-full border rounded-lg px-3 py-2 tracking-widest focus:ring-2 focus:ring-rose-200 focus:border-rose-400"
                               placeholder="6-digit OTP" required/>
                        <p class="text-xs text-gray-500 mt-1">Didn’t get OTP? <a href="ResendOtpServlet" class="text-teal-700 hover:underline">Resend</a></p>
                    </div>

                    <div class="flex items-center gap-3">
                        <button type="submit"
                                class="px-5 py-2.5 bg-rose-600 text-white rounded-lg shadow hover:bg-rose-700 transition">
                            Delete Account
                        </button>
                        <a href="${pageContext.request.contextPath}/dashboard"
                           class="px-5 py-2.5 border border-gray-300 rounded-lg hover:bg-gray-100">
                            Cancel
                        </a>
                    </div>
                </form>

                <div class="mt-8 text-sm text-gray-600">
                    <h2 class="font-semibold mb-2">What will be deleted?</h2>
                    <ul class="list-disc ml-5 space-y-1">
                        <li>User profile and login access</li>
                        <li>Accounts, incomes, expenses, budgets, targets</li>
                        <li>Transactions and liabilities data</li>
                    </ul>
                </div>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
