package controller.Reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import dao.DatabaseManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.UserSession;
import org.jfree.chart.*;
import java.util.List;
import java.util.ArrayList;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@WebServlet("/downloadReport")
public class DownloadReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserSession userSession = (session != null)
                ? (UserSession) session.getAttribute("userSession")
                : null;

        if (userSession == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = userSession.getUserId();
        String name = userSession.getName();   // ✅ Name
        String email = "";

        List<Map<String, Object>> incomes = new ArrayList<>();
        List<Map<String, Object>> expenses = new ArrayList<>();

        Map<YearMonth, Double> incomeByMonth = new LinkedHashMap<>();
        Map<YearMonth, Double> expenseByMonth = new LinkedHashMap<>();
        Map<String, Double> incomeByCategory = new LinkedHashMap<>();
        Map<String, Double> expenseByCategory = new LinkedHashMap<>();

        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            incomeByMonth.put(now.minusMonths(i), 0.0);
            expenseByMonth.put(now.minusMonths(i), 0.0);
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            // user email
            try (PreparedStatement ps = conn.prepareStatement("SELECT email FROM users WHERE user_id = ? ")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        email = rs.getString("email");
                    }
                }
            }

            // Income
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT income_date, income_source, amount FROM income WHERE user_id=? ORDER BY income_date DESC")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("date", rs.getDate("income_date"));
                        row.put("source", rs.getString("income_source"));
                        row.put("amount", rs.getBigDecimal("amount"));
                        incomes.add(row);
                    }
                }
            }

            // Expense
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT expense_date, expense_category, amount FROM expense WHERE user_id=? ORDER BY expense_date DESC")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("date", rs.getDate("expense_date"));
                        row.put("category", rs.getString("expense_category"));
                        row.put("amount", rs.getBigDecimal("amount"));
                        expenses.add(row);
                    }
                }
            }

            // Category summaries
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(income_source, 'Uncategorized'), SUM(amount) FROM income WHERE user_id=? GROUP BY income_source")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        incomeByCategory.put(rs.getString(1), rs.getDouble(2));
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(expense_category, 'Uncategorized'), SUM(amount) FROM expense WHERE user_id=? GROUP BY expense_category")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        expenseByCategory.put(rs.getString(1), rs.getDouble(2));
                    }
                }
            }

        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=Wealthory_Report.pdf");

        try {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();

            // ===== Fonts (iText Fonts, not AWT) =====
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(34, 197, 94));
            Font subFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            // ===== Header =====
            Paragraph title = new Paragraph("Wealthory - Transactions Report\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            doc.add(new Paragraph("Generated on: " + LocalDate.now(), normalFont));
            doc.add(new Paragraph("Name: " + (name != null ? name : "N/A"), normalFont));
            doc.add(new Paragraph("Email: " + (email != null ? email : "N/A") + "\n\n", normalFont));

            // ===== Income Table =====
            doc.add(new Paragraph("Income Records", subFont));
            doc.add(new Paragraph(" "));

            if (incomes.isEmpty()) {
                doc.add(new Paragraph("No income records found.\n\n"));
            } else {
                PdfPTable table = styledTable(new String[]{"Date", "Source", "Amount (₹)"});
                for (Map<String, Object> row : incomes) {
                    table.addCell(styledCell(String.valueOf(row.get("date")), Element.ALIGN_LEFT));
                    table.addCell(styledCell(String.valueOf(row.get("source")), Element.ALIGN_LEFT));
                    table.addCell(styledCell(String.valueOf(row.get("amount")), Element.ALIGN_RIGHT));
                }
                doc.add(table);
                doc.add(new Paragraph("\n"));
            }

            // ===== Expense Table =====
            doc.add(new Paragraph("Expense Records", subFont));
            doc.add(new Paragraph(" "));

            if (expenses.isEmpty()) {
                doc.add(new Paragraph("No expense records found.\n\n"));
            } else {
                PdfPTable table = styledTable(new String[]{"Date", "Category", "Amount (₹)"});
                for (Map<String, Object> row : expenses) {
                    table.addCell(styledCell(String.valueOf(row.get("date")), Element.ALIGN_LEFT));
                    table.addCell(styledCell(String.valueOf(row.get("category")), Element.ALIGN_LEFT));
                    table.addCell(styledCell(String.valueOf(row.get("amount")), Element.ALIGN_RIGHT));
                }
                doc.add(table);
                doc.add(new Paragraph("\n"));
            }

            // ===== Charts =====
            doc.add(new Paragraph("📊 Summary Charts", subFont));
            doc.add(new Paragraph(" "));

            // Line Chart (Income vs Expense)
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (YearMonth ym : incomeByMonth.keySet()) {
                String label = ym.getMonth().toString().substring(0, 3) + " " + ym.getYear();
                dataset.addValue(incomeByMonth.get(ym), "Income", label);
                dataset.addValue(expenseByMonth.get(ym), "Expense", label);
            }

            JFreeChart chart = ChartFactory.createLineChart(
                    "Income vs Expense (Last 6 Months)", "Month", "Amount (₹)", dataset,
                    PlotOrientation.VERTICAL, true, false, false);

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.white);
            plot.setDomainGridlinePaint(Color.lightGray);
            plot.setRangeGridlinePaint(Color.lightGray);
            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(34, 197, 94));
            renderer.setSeriesPaint(1, new Color(239, 68, 68));

            BufferedImage chartImage = chart.createBufferedImage(480, 300);
            Image img = Image.getInstance(chartImage, null);
            img.scaleToFit(500, 300);
            doc.add(img);

            doc.close();

        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }

    private PdfPTable styledTable(String[] headers) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(10);
        BaseColor headerColor = new BaseColor(34, 197, 94);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        return table;
    }

    private PdfPCell styledCell(String text, int align) {
        Font f = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", f));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        return cell;
    }
}
