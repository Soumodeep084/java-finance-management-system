package controller.Reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import dao.DatabaseManager;
import java.util.List;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.UserSession;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
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
        String name = userSession.getName();
        String email = ""; // make sure you set email in session

        List<Map<String, Object>> incomes = new ArrayList<>();
        List<Map<String, Object>> expenses = new ArrayList<>();

        // Fetch transactions
        try (Connection conn = DatabaseManager.getConnection()) {

            // user email
            try (PreparedStatement ps = conn.prepareStatement( "SELECT email FROM users WHERE user_id = ? ")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        email = rs.getString("email");
                    }
                }
            }
            
            // income records
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT income_date, income_source, amount FROM income WHERE user_id = ? ORDER BY income_date DESC")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("income_date", rs.getDate("income_date"));
                        map.put("income_source", rs.getString("income_source"));
                        map.put("amount", rs.getBigDecimal("amount"));
                        incomes.add(map);
                    }
                }
            }

            // expense records
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT expense_date, expense_category, amount FROM expense WHERE user_id = ? ORDER BY expense_date DESC")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("expense_date", rs.getDate("expense_date"));
                        map.put("expense_category", rs.getString("expense_category"));
                        map.put("amount", rs.getBigDecimal("amount"));
                        expenses.add(map);
                    }
                }
            }

        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }

        // Set PDF response headers
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=Wealthory_Report.pdf");

        try {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();

            // Fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(34, 197, 94)); // emerald
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(64, 64, 64));
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            Font grayFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, new BaseColor(120, 120, 120));

            // Logo (optional - place logo.png in WebContent/images)
            try {
                String logoPath = getServletContext().getRealPath("/images/sm-logo.png");
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(60, 60);
                logo.setAlignment(Element.ALIGN_CENTER);
                doc.add(logo);
            } catch (DocumentException | IOException ignored) {
                // skip if logo not found
            }

            // Title
            Paragraph title = new Paragraph("Wealthory - Transactions Report\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            // User Info
            doc.add(new Paragraph("Generated on: " + LocalDate.now(), grayFont));
            doc.add(new Paragraph("Name: " + name, normalFont));
            doc.add(new Paragraph("Email: " + (email != null ? email : "N/A") + "\n\n", normalFont));

            // Divider
            LineSeparator line = new LineSeparator();
            line.setLineColor(new BaseColor(200, 200, 200));
            doc.add(line);

            // Income Section
            doc.add(new Paragraph("\nIncome Records", subTitleFont));
            doc.add(new Paragraph(" "));

            if (incomes.isEmpty()) {
                doc.add(new Paragraph("⚠️ No income records found.\n\n", grayFont));
            } else {
                PdfPTable incomeTable = new PdfPTable(3);
                incomeTable.setWidthPercentage(100);
                incomeTable.setSpacingBefore(5);
                incomeTable.addCell("Date");
                incomeTable.addCell("Source");
                incomeTable.addCell("Amount");

                for (Map<String, Object> inc : incomes) {
                    incomeTable.addCell(String.valueOf(inc.get("income_date")));
                    incomeTable.addCell(String.valueOf(inc.get("income_source")));
                    incomeTable.addCell("₹" + inc.get("amount"));
                }
                doc.add(incomeTable);
            }

            // Expense Section
            doc.add(new Paragraph("\nExpense Records", subTitleFont));
            doc.add(new Paragraph(" "));

            if (expenses.isEmpty()) {
                doc.add(new Paragraph("⚠️ No expense records found.\n\n", grayFont));
            } else {
                PdfPTable expenseTable = new PdfPTable(3);
                expenseTable.setWidthPercentage(100);
                expenseTable.setSpacingBefore(5);
                expenseTable.addCell("Date");
                expenseTable.addCell("Category");
                expenseTable.addCell("Amount");

                for (Map<String, Object> exp : expenses) {
                    expenseTable.addCell(String.valueOf(exp.get("expense_date")));
                    expenseTable.addCell(String.valueOf(exp.get("expense_category")));
                    expenseTable.addCell("₹" + exp.get("amount"));
                }
                doc.add(expenseTable);
            }

            // Divider
            doc.add(new Paragraph("\n"));
            doc.add(line);

            // Summary Section
            doc.add(new Paragraph("\n📊 Summary & Graphs", subTitleFont));
            doc.add(new Paragraph("(Graphs will appear here once integrated.)", grayFont));

            if (incomes.isEmpty() && expenses.isEmpty()) {
                doc.add(new Paragraph("\nNo transactions found for this account.", grayFont));
            }

            doc.close();

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
