<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="fixed top-0 w-full bg-white/80 backdrop-blur-md z-50 border-b">
    <nav class="container mx-auto px-4 py-2 flex items-center justify-between relative">
        <!-- Logo -->
        <c:if test="${empty sessionScope.userSession}">
            <a href="index.jsp">
                <img src="images/sm-logo.png" alt="Finance Logo" class="h-16 w-auto object-contain"/>
            </a>
        </c:if>

        <c:if test="${not empty sessionScope.userSession}">
            <a href="${pageContext.request.contextPath}/dashboard">
                <img src="images/sm-logo.png" alt="Finance Logo" class="h-16 w-auto object-contain"/>
            </a>
        </c:if>

        <!-- Centered Welcome Text (only md and above, when logged in) -->
        <c:if test="${not empty sessionScope.userSession}">
            <span class="absolute left-1/2 transform -translate-x-1/2 text-emerald-500 text-xl font-semibold hidden md:block">
                Welcome to Wealthory !!
            </span>
        </c:if>

        <!-- Right-side action buttons / avatar -->
        <div class="flex items-center gap-4 ml-auto">
            <c:choose>
                <c:when test="${not empty sessionScope.userSession}">
                    <div class="flex items-center gap-3">
                        <!-- Dashboard Button -->
                        <a href="${pageContext.request.contextPath}/dashboard">
                            <button class="flex items-center gap-2 px-5 py-2 bg-white text-gray-800 border border-gray-300 rounded-full shadow hover:shadow-lg hover:bg-teal-50 transition transform hover:-translate-y-0.5">
                                🏠 <span class="hidden md:inline font-semibold">Dashboard</span>
                            </button>
                        </a>

                        <!-- Logout Button -->
                        <a href="logoutServlet" onclick="return confirm('Are you sure you want to log out?');">
                            <button class="flex items-center gap-2 px-5 py-2 bg-red-50 text-red-600 border border-red-400 rounded-full shadow hover:shadow-lg hover:bg-red-600 hover:text-white transition transform hover:-translate-y-0.5">
                                🔓 <span class="hidden md:inline font-semibold">Logout</span>
                            </button>
                        </a>
                    </div>


                    <div class="relative">
                        <!-- Avatar Button -->
                        <button id="avatarBtn" class="w-10 h-10 rounded-full overflow-hidden focus:outline-none">
                            <img src="images/avatar.png" alt="User" class="w-full h-full object-cover"/>
                        </button>

                        <!-- Dropdown Menu (hidden by default) -->
                        <div id="avatarDropdown" class="absolute right-0 mt-2 w-48 bg-white border border-gray-300 rounded shadow-lg hidden z-50">
                            <a href="${pageContext.request.contextPath}/change-username.jsp" class="flex items-center gap-2 px-4 py-2 text-emerald-700 hover:bg-gray-100">
                                👤 Change Username
                            </a>
                            <a href="${pageContext.request.contextPath}/change-password.jsp" class="flex items-center gap-2 px-4 py-2 text-orange-700 hover:bg-gray-100">
                                🔒 Change Password
                            </a>
                            <a href="DeleteAccountServlet" onclick="return confirm('Are you sure you want to delete your Account ?');" class="flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-100">
                                🗑️ Delete Account
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="login.jsp">
                        <button class="px-5 py-2 border border-teal-600 text-teal-600 rounded-lg font-semibold shadow-md hover:bg-teal-600 hover:text-white transition duration-300 ease-in-out">
                            Login
                        </button>
                    </a>
                    <a href="signup.jsp">
                        <button class="px-5 py-2 border border-red-600 text-red-600 rounded-lg font-semibold shadow-md hover:bg-red-600 hover:text-white transition duration-300 ease-in-out">
                            Sign Up
                        </button>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
</header>


<script>
    const avatarBtn = document.getElementById('avatarBtn');
    const avatarDropdown = document.getElementById('avatarDropdown');

    avatarBtn.addEventListener('click', () => {
        avatarDropdown.classList.toggle('hidden');
    });

    // Optional: click outside to close
    window.addEventListener('click', (e) => {
        if (!avatarBtn.contains(e.target) && !avatarDropdown.contains(e.target)) {
            avatarDropdown.classList.add('hidden');
        }
    });
</script>
