package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.List;

import implementor.ProductImplementor;
import model.Product_pojo;

@SuppressWarnings("serial")
@WebServlet("/ProductController") // ✅ Match this with your form action URL
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,     // 2MB
    maxFileSize = 1024 * 1024 * 10,          // 10MB
    maxRequestSize = 1024 * 1024 * 50        // 50MB
)
public class ProductController extends HttpServlet {

    private ProductImplementor service;

    @Override
    public void init() throws ServletException {
        service = new ProductImplementor();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equalsIgnoreCase(action)) {
            Product_pojo p = new Product_pojo();

            p.setSellerPortId(request.getParameter("seller_port_id"));
            p.setProductName(request.getParameter("product_name"));
            p.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            p.setPrice(Double.parseDouble(request.getParameter("price")));

            // Handle image upload
            Part filePart = request.getPart("product_image");
            String fileName = getFileName(filePart);
            String uploadPath = getServletContext().getRealPath("") + File.separator + "images" + File.separator + "products";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String imagePath = "images/products/" + fileName;
            filePart.write(uploadPath + File.separator + fileName);
            p.setImagePath(imagePath);

            // Add or Update
            String idStr = request.getParameter("product_id");
            if (idStr != null && !idStr.isEmpty()) {
                p.setProductId(Integer.parseInt(idStr));
                service.updateProduct(p);
            } else {
                service.addProduct(p);
            }

        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(request.getParameter("product_id"));
            service.deleteProduct(id);
        }

        response.sendRedirect("ProductController"); // ✅ URL must match @WebServlet
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sellerPortId = "S001"; // ✅ Replace later with session-based ID
        List<Product_pojo> list = service.getAllProductsBySeller(sellerPortId);

        request.setAttribute("productList", list);
        RequestDispatcher rd = request.getRequestDispatcher("list.jsp");
        rd.forward(request, response);
    }

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return new File(cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "")).getName();
            }
        }
        return null;
    }
}
