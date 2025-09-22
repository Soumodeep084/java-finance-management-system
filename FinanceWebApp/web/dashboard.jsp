<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Dashboard - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-white pt-24">
        <jsp:include page="/components/navbar.jsp"/>
        <main class="container mx-auto px-4 py-8">
            <h2 class="text-3xl font-bold text-teal-700 mb-6">Overview</h2>
            <c:if test="${not empty error}">
                <div class="bg-rose-100 text-rose-700 p-3 rounded mb-4">${error}</div>
            </c:if>

            <div class="flex flex-wrap justify-center gap-4">
                <a href="${pageContext.request.contextPath}/accounts"
                   class="flex flex-col items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-300
                   bg-white hover:bg-teal-50 text-gray-700 hover:text-teal-700 shadow-md hover:shadow-lg transition w-36">
                    <span class="text-3xl">🏦</span>
                    <span class="font-medium text-sm">Accounts</span>
                </a>

                <a href="${pageContext.request.contextPath}/income"
                   class="flex flex-col items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-300
                   bg-white hover:bg-teal-50 text-gray-700 hover:text-teal-700 shadow-md hover:shadow-lg transition w-36">
                    <span class="text-3xl">📈</span>
                    <span class="font-medium text-sm">Income</span>
                </a>

                <a href="${pageContext.request.contextPath}/expense"
                   class="flex flex-col items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-300
                   bg-white hover:bg-teal-50 text-gray-700 hover:text-teal-700 shadow-md hover:shadow-lg transition w-36">
                    <span class="text-3xl">💸</span>
                    <span class="font-medium text-sm">Expense</span>
                </a>

                <a href="${pageContext.request.contextPath}/budgets"
                   class="flex flex-col items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-300
                   bg-white hover:bg-teal-50 text-gray-700 hover:text-teal-700 shadow-md hover:shadow-lg transition w-36">
                    <span class="text-3xl">🗂️</span>
                    <span class="font-medium text-sm">Budgets</span>
                </a>
                   
                   <a href="${pageContext.request.contextPath}/liabilities"
                   class="flex flex-col items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-300
                   bg-white hover:bg-teal-50 text-gray-700 hover:text-teal-700 shadow-md hover:shadow-lg transition w-36">
                    <span class="text-3xl">⚖️️️</span>
                    <span class="font-medium text-sm">Liabilities</span>
                </a>


                <a href="${pageContext.request.contextPath}/charts"
                   class="flex flex-col items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-300
                   bg-white hover:bg-teal-50 text-gray-700 hover:text-teal-700 shadow-md hover:shadow-lg transition w-36">
                    <span class="text-3xl">📊</span>
                    <span class="font-medium text-sm">Charts</span>
                </a>
            </div>

            <div class="grid grid-cols-2 md:grid-cols-4 gap-6 mt-4">
                <div class="p-6 bg-white rounded shadow-xl border border-gray-200">
                    <div class="text-sm text-gray-500">Total Balance</div>
                    <div class="text-2xl font-bold text-blue-700">₹ <c:out value="${totalBalance}"/></div>
                </div>
                <div class="p-6 bg-white rounded shadow-xl border border-gray-200">
                    <div class="text-sm text-gray-500">Liabilities</div>
                    <div class="text-2xl font-bold text-blue-700">₹ <c:out value="${totalLiabilities}"/></div>
                </div>
                <div class="p-6 bg-white rounded shadow-xl border border-gray-200">
                    <div class="text-sm text-gray-500">Income (This month)</div>
                    <div class="text-2xl font-bold text-green-700">₹ <c:out value="${monthIncome}"/></div>
                </div>
                <div class="p-6 bg-white rounded shadow-xl border border-gray-200">
                    <div class="text-sm text-gray-500">Expense (This month)</div>
                    <div class="text-2xl font-bold text-rose-700">₹ <c:out value="${monthExpense}"/></div>
                </div>
            </div>
        </main>
        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
