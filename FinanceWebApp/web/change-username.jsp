<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Change Username - Wealthory</title>
  <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="pt-24 bg-gray-50">
  <jsp:include page="/components/navbar.jsp"/>

  <main class="container mx-auto px-4 py-10 max-w-lg">
    <div class="bg-white rounded-2xl shadow p-8">
      <h1 class="text-2xl font-bold text-teal-700 mb-6">Change Username</h1>

      <c:if test="${not empty error}">
        <div class="bg-rose-100 text-rose-700 px-4 py-3 rounded mb-4">${error}</div>
      </c:if>
      <c:if test="${not empty success}">
        <div class="bg-green-100 text-green-700 px-4 py-3 rounded mb-4">${success}</div>
      </c:if>

      <form action="ChangeUsernameServlet" method="post" class="space-y-4">
        <div>
          <label for="newUsername" class="block text-sm text-gray-600 mb-1">New username</label>
          <input id="newUsername" name="newUsername" type="text"
                 class="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-teal-300 focus:border-teal-500"
                 placeholder="Enter new username" required/>
          <p class="text-xs text-gray-500 mt-1">Must be 3–20 characters; letters, numbers, underscores.</p>
        </div>

        <button class="w-full bg-teal-700 text-white py-2.5 rounded-lg hover:bg-teal-800 transition">
          Update Username
        </button>
      </form>
    </div>
  </main>

  <jsp:include page="/components/footer.jsp"/>
</body>
</html>
