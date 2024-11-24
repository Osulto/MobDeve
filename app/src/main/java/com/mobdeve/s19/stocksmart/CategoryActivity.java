package com.mobdeve.s19.stocksmart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobdeve.s19.stocksmart.database.dao.CategoryDao;
import com.mobdeve.s19.stocksmart.database.models.Category;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView tempIconImageView;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private EditText searchEditText;
    private BottomNavigationView bottomNavigation;
    private CategoryDao categoryDao;
    private View emptyStateContainer;
    private FloatingActionButton fabAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categoryDao = new CategoryDao(this);
        setupImagePicker();

        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupBottomNavigation();
        setupFab();

        loadCategories();
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("category_name", category.getName());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Category category) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        TextInputEditText etCategoryName = dialogView.findViewById(R.id.etCategoryName);
        ImageView ivCategoryIcon = dialogView.findViewById(R.id.ivCategoryIcon);
        View btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);

        etCategoryName.setText(category.getName());

        // Load existing icon
        Bitmap existingIcon = BitmapFactory.decodeFile(category.getIconPath());
        if (existingIcon != null) {
            ivCategoryIcon.setImageBitmap(existingIcon);
        } else {
            ivCategoryIcon.setImageResource(R.drawable.placeholder_image);
        }

        selectedImageUri = null;
        tempIconImageView = ivCategoryIcon;

        btnSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Edit Category")
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String newName = etCategoryName.getText().toString().trim();

                if (newName.isEmpty()) {
                    etCategoryName.setError("Category name is required");
                    return;
                }

                try {
                    if (!newName.equals(category.getName()) && categoryDao.getByName(newName) != null) {
                        etCategoryName.setError("Category with this name already exists");
                        return;
                    }

                    // Update image only if a new one was selected
                    String newImagePath = category.getIconPath();
                    if (selectedImageUri != null) {
                        newImagePath = saveImageToInternalStorage(selectedImageUri);
                        if (newImagePath == null) {
                            showError("Failed to save category icon", null);
                            return;
                        }
                        // Delete old image file if it's different from the new one
                        File oldFile = new File(category.getIconPath());
                        if (oldFile.exists() && !oldFile.getAbsolutePath().equals(newImagePath)) {
                            oldFile.delete();
                        }
                    }

                    category.setName(newName);
                    category.setIconPath(newImagePath);

                    if (categoryDao.update(category)) {
                        adapter.updateCategory(category);
                        dialog.dismiss();
                    } else {
                        showError("Failed to update category", null);
                    }
                } catch (Exception e) {
                    showError("Failed to update category", e);
                }
            });
        });

        dialog.show();
    }
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (tempIconImageView != null && selectedImageUri != null) {
                            tempIconImageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.categoriesRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        fabAddCategory = findViewById(R.id.fabAddCategory);

        findViewById(R.id.btnAddFirstCategory).setOnClickListener(v -> showAddCategoryDialog());
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void showAddCategoryDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        TextInputEditText etCategoryName = dialogView.findViewById(R.id.etCategoryName);
        ImageView ivCategoryIcon = dialogView.findViewById(R.id.ivCategoryIcon);
        View btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);

        selectedImageUri = null;
        tempIconImageView = ivCategoryIcon;

        // Set default placeholder image
        ivCategoryIcon.setImageResource(R.drawable.placeholder_image);

        btnSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Add Category")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = etCategoryName.getText().toString().trim();

                if (name.isEmpty()) {
                    etCategoryName.setError("Category name is required");
                    return;
                }

                try {
                    if (categoryDao.getByName(name) != null) {
                        etCategoryName.setError("Category with this name already exists");
                        return;
                    }

                    String imagePath;
                    if (selectedImageUri != null) {
                        // User selected an image
                        imagePath = saveImageToInternalStorage(selectedImageUri);
                        if (imagePath == null) {
                            showError("Failed to save category icon", null);
                            return;
                        }
                    } else {
                        // Use placeholder image
                        imagePath = savePlaceholderImage();
                        if (imagePath == null) {
                            showError("Failed to save placeholder icon", null);
                            return;
                        }
                    }

                    Category category = new Category(name, imagePath);
                    long id = categoryDao.insert(category);
                    if (id != -1) {
                        category.setId(id);
                        if (adapter.getItemCount() == 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            searchEditText.setVisibility(View.VISIBLE);
                            emptyStateContainer.setVisibility(View.GONE);
                            fabAddCategory.setVisibility(View.VISIBLE);
                        }
                        adapter.addCategory(category);
                        dialog.dismiss();
                    } else {
                        showError("Failed to add category", null);
                    }
                } catch (Exception e) {
                    showError("Failed to add category", e);
                }
            });
        });

        dialog.show();
    }

    private void setupFab() {
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_products);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, HomeActivity.class);
            } else if (itemId == R.id.navigation_products) {
                return true;
            } else if (itemId == R.id.navigation_add) {
                intent = new Intent(this, AddStockActivity.class);
            } else if (itemId == R.id.navigation_reports) {
                intent = new Intent(this, ReportsActivity.class);
            } else if (itemId == R.id.navigation_settings) {
                intent = new Intent(this, SettingsActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            }
        }
    }


    private void loadCategories() {
        try {
            List<Category> categories = categoryDao.getAll();
            adapter.setCategories(categories);

            if (categories.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                searchEditText.setVisibility(View.GONE);
                emptyStateContainer.setVisibility(View.VISIBLE);
                fabAddCategory.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                searchEditText.setVisibility(View.VISIBLE);
                emptyStateContainer.setVisibility(View.GONE);
                fabAddCategory.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            showError("Failed to load categories", e);
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Scale down the bitmap if it's too large
            int maxSize = 512;
            if (bitmap.getWidth() > maxSize || bitmap.getHeight() > maxSize) {
                float ratio = Math.min(
                        (float) maxSize / bitmap.getWidth(),
                        (float) maxSize / bitmap.getHeight()
                );
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        Math.round(bitmap.getWidth() * ratio),
                        Math.round(bitmap.getHeight() * ratio),
                        true
                );
            }

            String filename = "category_icon_" + UUID.randomUUID().toString() + ".jpg";
            File file = new File(getFilesDir(), filename);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String savePlaceholderImage() {
        try {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.placeholder_image);
            if (drawable == null) return null;

            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            String filename = "category_icon_" + UUID.randomUUID().toString() + ".jpg";
            File file = new File(getFilesDir(), filename);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDeleteClick(Category category) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category? All products in this category will also be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        if (categoryDao.delete(category.getId())) {
                            // Delete the category icon file
                            File iconFile = new File(category.getIconPath());
                            if (iconFile.exists()) {
                                iconFile.delete();
                            }

                            adapter.removeCategory(category);
                            if (adapter.getItemCount() == 0) {
                                recyclerView.setVisibility(View.GONE);
                                searchEditText.setVisibility(View.GONE);
                                emptyStateContainer.setVisibility(View.VISIBLE);
                                fabAddCategory.setVisibility(View.GONE);
                            }
                        } else {
                            showError("Failed to delete category", null);
                        }
                    } catch (Exception e) {
                        showError("Failed to delete category", e);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showError(String message, Exception e) {
        String errorMessage = e != null ? message + ": " + e.getMessage() : message;
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any temporary resources
        tempIconImageView = null;
        selectedImageUri = null;
    }
}