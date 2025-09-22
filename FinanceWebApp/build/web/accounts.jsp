<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Accounts - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-white pt-24">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-6 py-8">
            <h1 class="text-3xl font-bold text-teal-700 mb-6 px-4">Accounts</h1>

            <c:if test="${not empty error}">
                <div class="bg-rose-100 text-rose-700 p-3 rounded mb-4 px-4">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="bg-green-100 text-green-700 p-3 rounded mb-4 px-4">${success}</div>
            </c:if>

            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 px-4">
                <form action="${pageContext.request.contextPath}/accounts" method="post" class="p-5 bg-gray-50 rounded border">
                    <input type="hidden" name="action" value="create"/>
                    <h3 class="text-lg font-semibold mb-3">Create Account</h3>
                    <div class="flex gap-3">
                        <input name="account_type" class="flex-1 border rounded px-3 py-2" placeholder="e.g., Savings" required/>
                        <button class="px-5 py-2 bg-teal-700 text-white rounded hover:bg-teal-800">Create</button>
                    </div>
                </form>

                <form action="${pageContext.request.contextPath}/accounts" method="post" class="p-5 bg-gray-50 rounded border">
                    <input type="hidden" name="action" value="delete"/>
                    <h3 class="text-lg font-semibold mb-3">Delete Account</h3>
                    <div class="flex gap-3">
                        <select name="account_type" class="flex-1 border rounded px-3 py-2" required>
                            <c:forEach var="a" items="${accounts}">
                                <option value="${a.account_type}">${a.account_type}</option>
                            </c:forEach>
                        </select>
                        <button class="px-5 py-2 border border-gray-300 rounded hover:bg-gray-100">Delete</button>
                    </div>
                </form>
            </div>

            <h3 class="text-2xl font-semibold px-5 text-gray-800 mt-8 mb-3">All Accounts</h3>
            <div class="overflow-x-auto px-5">
                <table class="min-w-full bg-white rounded shadow">
                    <thead>
                        <tr class="text-left bg-gray-50">
                            <th class="p-3">Account</th>
                            <th class="p-3">Balance</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="a" items="${accounts}">
                            <tr class="border-t">
                                <td class="p-3"><c:out value="${a.account_type}"/></td>
                                <td class="p-3">₹ <c:out value="${a.balance}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty accounts}">
                            <tr><td colspan="3" class="p-3 text-center text-gray-500">No accounts found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
