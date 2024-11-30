package com.mobdeve.s19.stocksmart;

import android.content.Context;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.dao.ProductDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import com.mobdeve.s19.stocksmart.database.models.Product;
import com.mobdeve.s19.stocksmart.utils.SessionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncManager {
    public interface SyncCallback {
        void onSuccess();
        void onError(String error);
    }

    private Context context;
    private FirebaseFirestore db;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private SessionManager sessionManager;

    public SyncManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.categoryDao = ((StockSmartApp) context.getApplicationContext()).getCategoryDao();
        this.productDao = ((StockSmartApp) context.getApplicationContext()).getProductDao();
        this.sessionManager = SessionManager.getInstance(context);
    }

    public void syncToCloud(SyncCallback callback) {
        String businessId = String.valueOf(sessionManager.getBusinessId());  // Convert long to String

        // First sync from cloud to local
        syncFromCloud(() -> {
            // Then sync local to cloud
            syncLocalToCloud(businessId, callback);
        }, callback);
    }

    private void syncFromCloud(Runnable onSuccess, SyncCallback callback) {
        String businessId = String.valueOf(sessionManager.getBusinessId());  // Convert long to String

        // Sync categories from cloud
        db.collection("businesses")
                .document(businessId)
                .collection("categories")
                .get()
                .addOnSuccessListener(categorySnapshots -> {
                    for (DocumentSnapshot doc : categorySnapshots.getDocuments()) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            Category category = new Category(
                                    data.get("name").toString(),
                                    data.get("iconPath").toString()
                            );
                            category.setBusinessId(Long.parseLong(businessId));
                            categoryDao.insert(category);
                        }
                    }

                    // Sync products from cloud
                    db.collection("businesses")
                            .document(businessId)
                            .collection("products")
                            .get()
                            .addOnSuccessListener(productSnapshots -> {
                                for (DocumentSnapshot doc : productSnapshots.getDocuments()) {
                                    Map<String, Object> data = doc.getData();
                                    if (data != null) {
                                        Product product = new Product();
                                        product.setName(data.get("name").toString());
                                        product.setCategoryId(Long.parseLong(data.get("categoryId").toString()));
                                        product.setQuantity(Integer.parseInt(data.get("quantity").toString()));
                                        product.setReorderPoint(Integer.parseInt(data.get("reorderPoint").toString()));
                                        product.setSellingPrice(Double.parseDouble(data.get("sellingPrice").toString()));
                                        product.setBusinessId(Long.parseLong(businessId));
                                        productDao.insert(product);
                                    }
                                }
                                onSuccess.run();
                            })
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private void syncLocalToCloud(String businessId, SyncCallback callback) {
        // Sync local data to cloud
        List<Category> categories = categoryDao.getAll();
        for (Category category : categories) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("name", category.getName());
            categoryData.put("iconPath", category.getIconPath());
            categoryData.put("businessId", businessId);

            db.collection("businesses")
                    .document(businessId)
                    .collection("categories")
                    .document(String.valueOf(category.getId()))
                    .set(categoryData, SetOptions.merge())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }

        List<Product> products = productDao.getAll();
        for (Product product : products) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("name", product.getName());
            productData.put("categoryId", product.getCategoryId());
            productData.put("quantity", product.getQuantity());
            productData.put("reorderPoint", product.getReorderPoint());
            productData.put("sellingPrice", product.getSellingPrice());
            productData.put("supplierPrice", product.getSupplierPrice());
            productData.put("businessId", businessId);

            db.collection("businesses")
                    .document(businessId)
                    .collection("products")
                    .document(String.valueOf(product.getId()))
                    .set(productData, SetOptions.merge())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }

        callback.onSuccess();
    }
}