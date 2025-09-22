<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Liabilities - Wealthory</title>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-white pt-24">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-5 py-8">
            <h1 class="text-3xl font-bold text-teal-700 mb-6">Liabilities</h1>

            <c:if test="${not empty error}">
                <div class="bg-rose-100 text-rose-700 p-3 rounded mb-4">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="bg-green-100 text-green-700 p-3 rounded mb-4">${success}</div>
            </c:if>

            <!-- Add liability -->
            <form action="${pageContext.request.contextPath}/liabilities" method="post" class="grid grid-cols-1 md:grid-cols-6 gap-4 bg-gray-50 p-4 rounded border">
                <input type="hidden" name="action" value="add"/>
                <div class="md:col-span-2">
                    <label class="block text-sm text-gray-600 mb-1">Title</label>
                    <input name="title" class="w-full border rounded px-3 py-2" placeholder="e.g., Credit Card" required/>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Amount</label>
                    <input type="number" step="0.01" min="0" name="amount" class="w-full border rounded px-3 py-2" required/>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Due Date</label>
                    <input type="date" name="due_date" class="w-full border rounded px-3 py-2"/>
                </div>
                <div>
                    <label class="block text-sm text-gray-600 mb-1">Status</label>
                    <select name="status" class="w-full border rounded px-3 py-2">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="PAID">PAID</option>
                        <option value="CLOSED">CLOSED</option>
                    </select>
                </div>
                <div class="md:col-span-2">
                    <label class="block text-sm text-gray-600 mb-1">Notes</label>
                    <input name="notes" class="w-full border rounded px-3 py-2" placeholder="Optional details"/>
                </div>
                <div class="md:col-span-6">
                    <button class="px-6 py-2 bg-teal-700 text-white rounded hover:bg-teal-800">Add Liability</button>
                </div>
            </form>

            <h3 class="text-2xl font-semibold text-gray-800 mt-8 mb-3">All Liabilities</h3>

            <jsp:useBean id="now" class="java.util.Date" />
            <fmt:formatDate value="${now}" pattern="yyyy-MM-dd" var="today"/>

            <div class="overflow-x-auto">
                <table class="min-w-full bg-white rounded shadow">
                    <thead>
                        <tr class="text-center bg-gray-50">
                            <th class="p-4">Title</th>
                            <th class="p-4">Amount</th>
                            <th class="p-4">Due</th>
                            <th class="p-4">Status</th>
                            <th class="p-4">Notes</th>
                            <th class="p-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="l" items="${liabilities}">
                            <c:set var="overdue" value="${not empty l.due_date and l.due_date < today and l.status ne 'PAID'}"/>

                            <tr class="border-t align-top ${overdue ? 'bg-rose-50 text-rose-800' : ''}">
                                <td colspan="6" class="p-0">
                                    <form action="${pageContext.request.contextPath}/liabilities" method="post" class="p-2">
                                        <input type="hidden" name="liability_id" value="${l.liability_id}"/>

                                        <div class="grid grid-cols-1 md:grid-cols-6 gap-2">
                                            <div>
                                                <input name="title" value="${l.title}"
                                                       class="w-full border rounded px-2 py-1 ${overdue ? 'border-rose-300 bg-rose-50' : ''}"/>
                                            </div>
                                            <div>
                                                <input type="number" step="0.01" min="0" name="amount" value="${l.amount}"
                                                       class="w-full border rounded px-2 py-1 ${overdue ? 'border-rose-300 bg-rose-50' : ''}"/>
                                            </div>
                                            <div>
                                                <input type="date" name="due_date" value="${l.due_date}"
                                                       class="w-full border rounded px-2 py-1 ${overdue ? 'border-rose-300 bg-rose-50' : ''}"/>
                                            </div>
                                            <div>
                                                <select name="status"
                                                        class="w-full border rounded px-2 py-1 ${overdue ? 'border-rose-300 bg-rose-50' : ''}">
                                                    <option ${l.status=='ACTIVE'?'selected':''} value="ACTIVE">ACTIVE</option>
                                                    <option ${l.status=='PAID'?'selected':''} value="PAID">PAID</option>
                                                    <option ${l.status=='CLOSED'?'selected':''} value="CLOSED">CLOSED</option>
                                                </select>
                                            </div>
                                            <div>
                                                <input name="notes" value="${l.notes}"
                                                       class="w-full border rounded px-2 py-1 ${overdue ? 'border-rose-300 bg-rose-50' : ''}"/>
                                            </div>
                                            <div class="flex justify-center gap-4 ">
                                                <button name="action" value="update" class="text-sm relative inline-flex items-center justify-center px-4 py-2 text-white font-semibold tracking-wide rounded-xl shadow-md bg-gradient-to-r from-blue-500 to-blue-700 hover:from-blue-600 hover:to-blue-800 focus:outline-none focus:ring-4 focus:ring-blue-300 transition-all duration-200 ease-in-out">  
                                                    Save
                                                </button>

                                                <button name="action" value="delete" class="text-sm relative inline-flex items-center justify-center px-4 py-2 font-semibold tracking-wide rounded-xl shadow-md border border-rose-400 text-rose-600 hover:bg-rose-50 hover:shadow-lg focus:outline-none focus:ring-4 focus:ring-rose-200 transition-all duration-200 ease-in-out">
                                                    Delete
                                                </button>
                                            </div>

                                        </div>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty liabilities}">
                            <tr><td colspan="6" class="p-3 text-center text-gray-500">No liabilities yet.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
