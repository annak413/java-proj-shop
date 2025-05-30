const contentDiv = document.getElementById('content');

// Function to load and display the products section
function showProductsWithFilter() {
    fetch('products.html')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text();
        })
        .then(html => {
            contentDiv.innerHTML = html;

            // Initialize product-related functionalities after content is loaded
            fetchCategoriesForProductFilterDropdown(); // Populate category filter dropdown
            fetchCategoriesForProductFormDropdownNew(); // Populate category dropdown in product form

            const productAddEditForm = document.getElementById('product-add-edit-form');
            if (productAddEditForm) {
                productAddEditForm.addEventListener('submit', handleAddProductNew);
            } else {
                console.error("Елемент 'product-add-edit-form' не знайдено в products.html. Перевірте ID форми.");
            }

            fetchProductsNew(); // Load initial product list
        })
        .catch(error => {
            console.error('Помилка завантаження products.html:', error);
            contentDiv.innerHTML = `<p>Не вдалося завантажити сторінку продуктів. Будь ласка, спробуйте ще раз.</p>`;
        });
}

// Function to load and display the customers section
function showCustomers() {
    fetch('customers.html')
        .then(response => response.text())
        .then(html => {
            contentDiv.innerHTML = html;
            fetchCustomers(); // Load initial customer list
            const addCustomerForm = document.getElementById('add-customer-form');
            if (addCustomerForm) {
                addCustomerForm.addEventListener('submit', handleAddCustomer);
            }
        });
}

// Function to load and display the categories section
function showCategories() {
    fetch('categories.html')
        .then(response => response.text())
        .then(html => {
            contentDiv.innerHTML = html;
            fetchCategories(); // Load initial category list
            const addCategoryForm = document.getElementById('add-category-form');
            if (addCategoryForm) {
                addCategoryForm.addEventListener('submit', handleAddCategory);
            }
        });
}

// =====================================================================================================================
// PRODUCT FUNCTIONS
// =====================================================================================================================

// Shows the add/edit product form
function showAddProductFormNew() {
    const addProductFormContainer = document.getElementById('add-product-form');
    const productAddEditForm = document.getElementById('product-add-edit-form');

    if (addProductFormContainer && productAddEditForm) {
        addProductFormContainer.style.display = 'block';
        productAddEditForm.style.display = 'block';

        // Clear form and set title for new product, if ID is empty
        if (document.getElementById('id').value === '') {
            productAddEditForm.reset();
            document.getElementById('id').value = '';
            const imageUrlsInput = document.getElementById('imageUrls');
            if (imageUrlsInput) imageUrlsInput.value = '';
            productAddEditForm.querySelector('h2').textContent = 'Додати новий продукт';
        }

        fetchCategoriesForProductFormDropdownNew(); // Always load categories for the dropdown
    } else {
        console.error("Елемент 'add-product-form' або 'product-add-edit-form' не знайдено. Перевірте, чи є він у products.html.");
    }
}

// Loads product data into the form for editing
async function handleEditProductNew(productId) {
    try {
        const response = await fetch(`/api/products/dto/${productId}`);
        if (!response.ok) {
            throw new Error(`Помилка завантаження продукту для редагування: ${response.status} ${response.statusText}`);
        }
        const product = await response.json();

        // Populate form fields
        document.getElementById('id').value = product.id;
        document.getElementById('name').value = product.name;
        document.getElementById('price').value = product.price;
        document.getElementById('producer').value = product.producer;
        document.getElementById('countryOfOrigin').value = product.countryOfOrigin || '';
        document.getElementById('weight').value = product.weight;
        document.getElementById('description').value = product.description;

        // Set category dropdown
        const categorySelect = document.getElementById('category');
        await fetchCategoriesForProductFormDropdownNew(); // Ensure categories are loaded
        if (product.categoryId) {
            categorySelect.value = product.categoryId;
        } else {
            categorySelect.value = '';
        }

        // Set image URLs
        const imageUrlsInput = document.getElementById('imageUrls');
        if (product.imageUrls && product.imageUrls.length > 0) {
            imageUrlsInput.value = product.imageUrls.join(', ');
        } else {
            imageUrlsInput.value = '';
        }

        // Change form title
        document.getElementById('product-add-edit-form').querySelector('h2').textContent = `Редагувати продукт: ${product.name}`;

        showAddProductFormNew(); // Display the form
    } catch (error) {
        console.error('Помилка при завантаженні продукту для редагування:', error);
        alert('Не вдалося завантажити продукт для редагування: ' + error.message);
    }
}

// Hides the add/edit product form and resets it
function cancelAddProductNew() {
    const addProductFormContainer = document.getElementById('add-product-form');
    const productAddEditForm = document.getElementById('product-add-edit-form');
    if (addProductFormContainer && productAddEditForm) {
        addProductFormContainer.style.display = 'none';
        productAddEditForm.reset();
        const categorySelect = document.getElementById('category');
        if (categorySelect) categorySelect.value = '';
        const imageUrlsInput = document.getElementById('imageUrls');
        if (imageUrlsInput) imageUrlsInput.value = '';
        document.getElementById('id').value = ''; // Clear hidden ID
    }
}

// Fetches categories to populate the product filter dropdown
async function fetchCategoriesForProductFilterDropdown() {
    try {
        const response = await fetch('/api/categories');
        const categories = await response.json();
        const categoryFilterSelect = document.getElementById('category-filter');

        categoryFilterSelect.innerHTML = '<option value="">Усі категорії</option>';

        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category.id;
            option.textContent = category.name;
            categoryFilterSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Помилка завантаження категорій для фільтра:', error);
    }
}

// Fetches categories to populate the dropdown in the product add/edit form
async function fetchCategoriesForProductFormDropdownNew() {
    try {
        const response = await fetch('/api/categories');
        const categories = await response.json();
        const categorySelect = document.getElementById('category');

        categorySelect.innerHTML = '<option value="">-- Виберіть категорію --</option>';

        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category.id;
            option.textContent = category.name;
            categorySelect.appendChild(option);
        });
    } catch (error) {
        console.error('Помилка завантаження категорій для форми продукту:', error);
    }
}

// Fetches and displays products, with optional category filtering
async function fetchProductsNew(categoryId = null) {
    try {
        let url = '/api/products/dto';
        if (categoryId) {
            url = `/api/products/category/${categoryId}`;
        }

        const response = await fetch(url);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Помилка завантаження продуктів: ${response.status} ${response.statusText} - ${errorText}`);
        }
        const products = await response.json();
        let productListHTML = '<ul>';
        if (products.length === 0) {
            productListHTML += '<p>Немає продуктів у цій категорії або список порожній.</p>';
        } else {
            products.forEach(product => {
                const imageUrl = (product.imageUrls && product.imageUrls.length > 0) ? product.imageUrls[0] : 'https://via.placeholder.com/50x50?text=No+Image';
                productListHTML += `
                    <li>
                        <img src="${imageUrl}" alt="${product.name}" style="width:50px; height:50px; object-fit:cover; margin-right:10px;">
                        ${product.name} (${product.categoryName ? product.categoryName : 'Без категорії'}) - ${product.price} грн
                        <button onclick="handleEditProductNew(${product.id})">Редагувати</button>
                        <button onclick="handleDeleteProductNew(${product.id})">Видалити</button>
                    </li>`;
            });
        }
        productListHTML += '</ul>';
        const productListDiv = document.getElementById('product-list');
        if (productListDiv) {
            productListDiv.innerHTML = productListHTML;
        } else {
            console.error("Елемент 'product-list' не знайдено в products.html. Перевірте ID.");
        }
    } catch (error) {
        console.error(`Помилка завантаження продуктів: ${error.message}`);
        const productListDiv = document.getElementById('product-list');
        if (productListDiv) {
            productListDiv.innerHTML = `<p>Не вдалося завантажити продукти: ${error.message}</p>`;
        }
    }
}

// Filters products based on the selected category
function filterProductsByCategory() {
    const categoryFilterSelect = document.getElementById('category-filter');
    const selectedCategoryId = categoryFilterSelect.value;

    if (selectedCategoryId === "") {
        fetchProductsNew(); // Fetch all products if "All Categories" is selected
    } else {
        fetchProductsNew(selectedCategoryId); // Fetch products by selected category
    }
}

// Handles adding or updating a product
async function handleAddProductNew(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const productData = {};

    for (const [key, value] of formData.entries()) {
        productData[key] = value;
    }

    productData.price = parseFloat(productData.price);
    productData.weight = parseFloat(productData.weight);

    const categoryId = productData.category;
    if (categoryId) {
        productData.categoryId = parseInt(categoryId);
        delete productData.category;
    } else {
        productData.categoryId = null;
        delete productData.category;
    }

    const imageUrlsInput = document.getElementById('imageUrls').value;
    if (imageUrlsInput) {
        productData.imageUrls = imageUrlsInput.split(',').map(url => url.trim()).filter(url => url !== '');
    } else {
        productData.imageUrls = [];
    }

    const productId = productData.id;
    const method = productId ? 'PUT' : 'POST';
    const url = productId ? `/api/products/dto/${productId}` : '/api/products/dto';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(productData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Помилка збереження продукту: ${response.status} ${response.statusText} - ${errorText}`);
        }

        form.reset();
        cancelAddProductNew(); // Hide and reset the form
        fetchProductsNew(); // Refresh product list
        alert('Продукт успішно збережено!');
    } catch (error) {
        console.error('Помилка збереження продукту:', error);
        alert('Не вдалося зберегти продукт: ' + error.message);
    }
}

// Handles deleting a product
async function handleDeleteProductNew(productId) {
    if (!confirm('Ви впевнені, що хочете видалити цей продукт?')) {
        return;
    }

    try {
        const response = await fetch(`/api/products/${productId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Помилка видалення продукту: ${response.status} ${response.statusText} - ${errorText}`);
        }

        alert('Продукт успішно видалено!');
        fetchProductsNew(); // Refresh product list
    } catch (error) {
        console.error('Помилка видалення продукту:', error);
        alert('Не вдалося видалити продукт: ' + error.message);
    }
}


// =====================================================================================================================
// CUSTOMER FUNCTIONS
// =====================================================================================================================

// Shows the add customer form
function showAddCustomerForm() {
    const addCustomerForm = document.getElementById('add-customer-form');
    if (addCustomerForm) {
        addCustomerForm.style.display = 'block';
        addCustomerForm.reset(); // Clear form on display for new customer
    }
}

// Fetches and displays customers
async function fetchCustomers() {
    try {
        const response = await fetch('/api/customers');
        const customers = await response.json();
        let customerListHTML = '<ul>';
        if (customers.length === 0) {
            customerListHTML += '<p>Немає клієнтів.</p>';
        } else {
            customers.forEach(customer => {
                customerListHTML += `<li>${customer.name} (${customer.email}) <button onclick="handleDeleteCustomer(${customer.id})">Видалити</button></li>`;
            });
        }
        customerListHTML += '</ul>';
        const customerListDiv = document.getElementById('customer-list');
        if (customerListDiv) {
            customerListDiv.innerHTML = customerListHTML;
        }
    } catch (error) {
        console.error('Помилка при завантаженні клієнтів:', error);
        const customerListDiv = document.getElementById('customer-list');
        if (customerListDiv) {
            customerListDiv.innerHTML = `<p>Не вдалося завантажити клієнтів: ${error.message}</p>`;
        }
    }
}

// Handles adding a new customer
async function handleAddCustomer(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const customerData = Object.fromEntries(formData.entries());

    try {
        const response = await fetch('/api/customers', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(customerData),
        });

        if (response.ok) {
            fetchCustomers(); // Refresh customer list
            form.reset();
            form.style.display = 'none'; // Hide the form after successful submission
            alert('Клієнт успішно доданий!');
        } else {
            const errorText = await response.text();
            console.error('Помилка при додаванні клієнта:', response.statusText, errorText);
            alert(`Не вдалося додати клієнта: ${errorText}`);
        }
    } catch (error) {
        console.error('Помилка:', error);
        alert('Виникла помилка при додаванні клієнта.');
    }
}

// Handles deleting a customer
async function handleDeleteCustomer(id) {
    if (confirm('Ви впевнені, що хочете видалити цього клієнта?')) {
        try {
            const response = await fetch(`/api/customers/${id}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                fetchCustomers(); // Refresh customer list
                alert('Клієнт успішно видалений!');
            } else {
                const errorText = await response.text();
                console.error('Помилка при видаленні клієнта:', response.statusText, errorText);
                alert(`Не вдалося видалити клієнта: ${errorText}`);
            }
        } catch (error) {
            console.error('Помилка:', error);
            alert('Виникла помилка при видаленні клієнта.');
        }
    }
}


// =====================================================================================================================
// CATEGORY FUNCTIONS
// =====================================================================================================================

// Shows the add category form
function showAddCategoryForm() {
    const addCategoryForm = document.getElementById('add-category-form');
    if (addCategoryForm) {
        addCategoryForm.style.display = 'block';
        addCategoryForm.reset(); // Clear form on display for new category
    }
}

// Fetches and displays categories
async function fetchCategories() {
    try {
        const response = await fetch('/api/categories');
        const categories = await response.json();
        let categoryListHTML = '<ul>';
        if (categories.length === 0) {
            categoryListHTML += '<p>Немає категорій.</p>';
        } else {
            categories.forEach(category => {
                categoryListHTML += `<li>${category.name} <button onclick="handleDeleteCategory(${category.id})">Видалити</button></li>`;
            });
        }
        categoryListHTML += '</ul>';
        const categoryListDiv = document.getElementById('category-list');
        if (categoryListDiv) {
            categoryListDiv.innerHTML = categoryListHTML;
        }
    } catch (error) {
        console.error('Помилка при завантаженні категорій:', error);
        const categoryListDiv = document.getElementById('category-list');
        if (categoryListDiv) {
            categoryListDiv.innerHTML = `<p>Не вдалося завантажити категорії: ${error.message}</p>`;
        }
    }
}

// Handles adding a new category
async function handleAddCategory(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const categoryData = Object.fromEntries(formData.entries());

    try {
        const response = await fetch('/api/categories', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(categoryData),
        });

        if (response.ok) {
            fetchCategories(); // Refresh category list
            form.reset();
            form.style.display = 'none'; // Hide the form after successful submission
            alert('Категорія успішно додана!');
        } else {
            const errorText = await response.text();
            console.error('Помилка при додаванні категорії:', response.statusText, errorText);
            alert(`Не вдалося додати категорію: ${errorText}`);
        }
    } catch (error) {
        console.error('Помилка:', error);
        alert('Виникла помилка при додаванні категорії.');
    }
}

// Handles deleting a category
async function handleDeleteCategory(id) {
    if (confirm('Ви впевнені, що хочете видалити цю категорію?')) {
        try {
            const response = await fetch(`/api/categories/${id}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                fetchCategories(); // Refresh category list
                alert('Категорія успішно видалена!');
            } else {
                const errorText = await response.text();
                console.error('Помилка при видаленні категорії:', response.statusText, errorText);
                alert(`Не вдалося видалити категорію: ${errorText}`);
            }
        } catch (error) {
            console.error('Помилка:', error);
            alert('Виникла помилка при видаленні категорії.');
        }
    }
}

// Initialize the product page with filter on load
window.onload = showProductsWithFilter;






 
 
 
 
 