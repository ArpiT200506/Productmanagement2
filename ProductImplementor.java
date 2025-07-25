package implementor;

import db_config.GetConnection;
import model.Product_pojo;
import operations.ProductOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImplementor implements ProductOperations {

	@Override
	public boolean addProduct(Product_pojo pojo) {
		String sql = "CALL add_product(?, ?, ?, ?, ?)";
		try (Connection con = GetConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, pojo.getSellerPortId());
			ps.setString(2, pojo.getProductName());
			ps.setInt(3, pojo.getQuantity());
			ps.setDouble(4, pojo.getPrice());
			ps.setString(5, pojo.getImagePath());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateProduct(Product_pojo pojo) {
		String sql = "CALL update_product(?, ?, ?, ?, ?, ?)";
		try (Connection con = GetConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, pojo.getProductId());
			ps.setString(2, pojo.getSellerPortId());
			ps.setString(3, pojo.getProductName());
			ps.setInt(4, pojo.getQuantity());
			ps.setDouble(5, pojo.getPrice());
			ps.setString(6, pojo.getImagePath());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override

	public boolean deleteProduct(int productId) {
		boolean result = false;
		try (Connection con = GetConnection.getConnection()) {
			if (con == null)
				throw new RuntimeException("Connection is null");

			CallableStatement cs = con.prepareCall("{CALL delete_product(?)}");
			cs.setInt(1, productId);
			result = cs.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Product_pojo> getAllProductsBySeller(String sellerPortId) {
		List<Product_pojo> list = new ArrayList<>();
		String sql = "CALL list_products_by_seller(?)";

		try (Connection con = GetConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, sellerPortId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product_pojo pojo = new Product_pojo();
				pojo.setProductId(rs.getInt("product_id"));
				pojo.setSellerPortId(rs.getString("seller_port_id"));
				pojo.setProductName(rs.getString("product_name"));
				pojo.setQuantity(rs.getInt("quantity"));
				pojo.setPrice(rs.getDouble("price"));
				pojo.setImagePath(rs.getString("image_path"));
				pojo.setDescription(rs.getString("description"));
				list.add(pojo);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}
