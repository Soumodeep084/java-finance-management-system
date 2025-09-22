<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Budgets & Target - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-white pt-24">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-4 py-8">
            <h1 class="text-3xl font-bold text-teal-700 mb-6">Budgets & Target</h1>

            <c:if test="${not empty error}">
                <div class="bg-rose-100 text-rose-700 p-3 rounded mb-4">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="bg-green-100 text-green-700 p-3 rounded mb-4">${success}</div>
            </c:if>

            <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <form action="${pageContext.request.contextPath}/budgets" method="post" class="p-5 bg-gray-50 rounded border">
                    <input type="hidden" name="action" value="add"/>
                    <h3 class="text-lg font-semibold mb-3">Add Budget</h3>
                    <div class="space-y-3">
                        <input name="expense_category" class="w-full border rounded px-3 py-2" placeholder="Category" required/>
                        <input type="number" step="0.01" min="0" name="amount" class="w-full border rounded px-3 py-2" placeholder="Amount" required/>
                        <button class="px-5 py-2 bg-teal-700 text-white rounded hover:bg-teal-800">Add</button>
                    </div>
                </form>

                <form action="${pageContext.request.contextPath}/budgets" method="post" class="p-5 bg-gray-50 rounded border">
                    <input type="hidden" name="action" value="delete"/>
                    <h3 class="text-lg font-semibold mb-3">Delete Budget</h3>
                    <div class="space-y-3">
                        <select name="expense_category" class="w-full border rounded px-3 py-2" required>
                            <c:forEach var="b" items="${budgets}">
                                <option value="${b.expense_category}">${b.expense_category}</option>
                            </c:forEach>
                        </select>
                        <button class="px-5 py-2 border border-gray-300 rounded hover:bg-gray-100">Delete</button>
                    </div>
                </form>

                <form action="${pageContext.request.contextPath}/budgets" method="post" class="p-5 bg-gray-50 rounded border">
                    <input type="hidden" name="action" value="target"/>
                    <h3 class="text-lg font-semibold mb-3">Monthly Target</h3>
                    <div class="space-y-3">
                        <input type="number" step="0.01" min="0" name="target_amount" class="w-full border rounded px-3 py-2" placeholder="Target amount" required/>
                        <button class="px-5 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700">Save Target</button>
                    </div>
                </form>
            </div>

            <h3 class="text-2xl font-semibold text-gray-800 mt-8 mb-3">Current Budgets</h3>
            <div class="overflow-x-auto">
                <table class="min-w-full bg-white rounded shadow">
                    <thead>
                        <tr class="text-left bg-gray-50">
                            <th class="p-3">Category</th>
                            <th class="p-3">Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${budgets}">
                            <tr class="border-t">
                                <td class="p-3"><c:out value="${b.expense_category}"/></td>
                                <td class="p-3">₹ <c:out value="${b.amount}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty budgets}">
                            <tr><td colspan="2" class="p-3 text-center text-gray-500">No budgets yet.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
