<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Expenses - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-white pt-24">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-4 py-8">
            <h1 class="text-3xl font-bold text-teal-700 mb-6">Expenses</h1>

            <c:if test="${not empty error}">
                <div class="bg-rose-100 text-rose-700 p-3 rounded mb-4">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="bg-green-100 text-green-700 p-3 rounded mb-4">${success}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/expense" method="post" class="grid grid-cols-1 md:grid-cols-5 gap-4 bg-gray-50 p-4 rounded border">
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Account</label>
                    <select name="account_type" class="w-full border rounded px-3 py-2" required>
                        <c:forEach var="a" items="${accounts}">
                            <option value="${a.account_type}">${a.account_type}</option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Category</label>
                    <input name="expense_category" class="w-full border rounded px-3 py-2" placeholder="Food, Rent..." required/>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Amount</label>
                    <input type="number" step="0.01" min="0" name="amount" class="w-full border rounded px-3 py-2" required/>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Date</label>
                    <input type="date" name="expense_date" class="w-full border rounded px-3 py-2" required/>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Remark</label>
                    <input name="remark" class="w-full border rounded px-3 py-2" placeholder="Optional note"/>
                </div>
                <div class="md:col-span-5">
                    <button class="px-6 py-2 bg-teal-700 text-white rounded hover:bg-teal-800">Add Expense</button>
                </div>
            </form>

            <h3 class="text-2xl font-semibold text-gray-800 mt-8 mb-3">Recent Expenses</h3>
            <div class="overflow-x-auto">
                <table class="min-w-full bg-white rounded shadow">
                    <thead>
                        <tr class="text-left bg-gray-50">
                            <th class="p-3">Date</th>
                            <th class="p-3">Category</th>
                            <th class="p-3">Account</th>
                            <th class="p-3">Amount</th>
                            <th class="p-3">Remark</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="e" items="${expenses}">
                            <tr class="border-t">
                                <td class="p-3"><c:out value="${e.expense_date}"/></td>
                                <td class="p-3"><c:out value="${e.expense_category}"/></td>
                                <td class="p-3"><c:out value="${e.account_type}"/></td>
                                <td class="p-3 text-rose-700 font-semibold">₹ <c:out value="${e.amount}"/></td>
                                <td class="p-3"><c:out value="${e.remark}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty expenses}">
                            <tr><td colspan="5" class="p-3 text-center text-gray-500">No expense records.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
