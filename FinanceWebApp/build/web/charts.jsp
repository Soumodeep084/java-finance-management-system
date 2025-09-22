<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Charts - Wealthory</title>
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <link rel="icon" type="image/png" href="images/sm-logo.png" sizes="32x32">
        <script src="https://cdn.tailwindcss.com"></script>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            /* Optional: slightly reduce legend font */
            @media (min-width: 1024px) {
                .chart-legend-small canvas {
                    max-height: 260px;
                }
            }
        </style>
    </head>
    <body class="bg-gray-50 pt-24">
        <jsp:include page="/components/navbar.jsp"/>

        <main class="container mx-auto px-4 pb-12">
            <h1 class="text-2xl font-bold text-teal-700 mb-4">Charts</h1>

            <c:if test="${not empty error}">
                <div class="bg-rose-100 text-rose-700 px-4 py-3 rounded mb-6">${error}</div>
            </c:if>

            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <!-- Line chart larger -->
                <div class="bg-white rounded-xl shadow p-4">
                    <h2 class="font-semibold mb-2">Income vs Expense (Last 6 Months)</h2>
                    <div class="w-full h-64 lg:h-80">
                        <canvas id="lineChart"></canvas>
                    </div>
                </div>

                <!-- Income pie smaller -->
                <div class="bg-white rounded-xl shadow p-4">
                    <h2 class="font-semibold mb-2">Income by Category (90 days)</h2>
                    <div class="w-full max-w-sm h-56 sm:h-60 mx-auto chart-legend-small">
                        <canvas id="incomePie"></canvas>
                    </div>
                </div>

                <!-- Expense pie smaller -->
                <div class="bg-white rounded-xl shadow p-4">
                    <h2 class="font-semibold mb-2">Expense by Category (90 days)</h2>
                    <div class="w-full max-w-sm h-56 sm:h-60 mx-auto chart-legend-small">
                        <canvas id="expensePie"></canvas>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="/components/footer.jsp"/>

        <script>
            // Pull JSON prepared by servlet (already valid JSON strings)
            const lineData = JSON.parse('${lineData != null ? lineData : "{}"}');
            const incomePie = JSON.parse('${incomePie != null ? incomePie : "{}"}');
            const expensePie = JSON.parse('${expensePie != null ? expensePie : "{}"}');

            const palette = ["#0ea5e9", "#22c55e", "#f97316", "#ef4444", "#a855f7", "#06b6d4", "#84cc16", "#eab308", "#f43f5e", "#14b8a6"];

            // Shared options
            const pieOpts = {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '60%',
                layout: {padding: 8},
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {usePointStyle: true, boxWidth: 8, padding: 8, font: {size: 11}}
                    },
                    tooltip: {callbacks: {label: (ctx) => `${ctx.label}: ${ctx.parsed}`}}
                            }
                        };

                        if (lineData.labels) {
                            new Chart(document.getElementById('lineChart').getContext('2d'), {
                                type: 'line',
                                data: {
                                    labels: lineData.labels,
                                    datasets: [
                                        {
                                            label: lineData.datasets[0].label,
                                            data: lineData.datasets[0].data,
                                            borderColor: "#16a34a",
                                            backgroundColor: "rgba(22,163,74,0.2)",
                                            tension: 0.25,
                                            fill: true
                                        },
                                        {
                                            label: lineData.datasets[1].label,
                                            data: lineData.datasets[1].data,
                                            borderColor: "#dc2626",
                                            backgroundColor: "rgba(220,38,38,0.2)",
                                            tension: 0.25,
                                            fill: true
                                        }
                                    ]
                                },
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    scales: {y: {beginAtZero: true}},
                                    plugins: {legend: {position: 'top'}}
                                }
                            });
                        }

                        if (incomePie.labels) {
                            new Chart(document.getElementById('incomePie').getContext('2d'), {
                                type: 'doughnut',
                                data: {labels: incomePie.labels, datasets: [{data: incomePie.data, backgroundColor: palette}]},
                                options: pieOpts
                            });
                        }

                        if (expensePie.labels) {
                            new Chart(document.getElementById('expensePie').getContext('2d'), {
                                type: 'doughnut',
                                data: {labels: expensePie.labels, datasets: [{data: expensePie.data, backgroundColor: palette}]},
                                options: pieOpts
                            });
                        }
        </script>
    </body>
</html>
