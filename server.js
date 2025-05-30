const express = require('express');
const cors = require('cors'); // Install: npm install cors
const app = express();
const PORT = 3000;

app.use(cors()); // Enable CORS for your frontend
app.use(express.json()); // To parse JSON request bodies

// --- Mock Data (Replace with a real database in production) ---
let products = [
    { id: 'p1', name: 'Смартфон X', price: 10000, manufacturer: 'TechCorp', origin: 'Китай', categoryId: 'c1' },
    { id: 'p2', name: 'Футболка бавовняна', price: 500, manufacturer: 'FashionCo', origin: 'Туреччина', categoryId: 'c2' },
    { id: 'p3', name: 'Кава Арабіка 250г', price: 250, manufacturer: 'CoffeeBeans', origin: 'Бразилія', categoryId: 'c3' },
    { id: 'p4', name: 'Ноутбук Pro', price: 25000, manufacturer: 'CompMaster', origin: 'США', categoryId: 'c1' }
];

let categories = [
    { id: 'c1', name: 'Електроніка' },
    { id: 'c2', name: 'Одяг' },
    { id: 'c3', name: 'Продукти харчування' }
];
// --- End Mock Data ---

// API Endpoint for Categories
app.get('/api/categories', (req, res) => {
    res.json(categories);
});

// API Endpoint for Products
app.get('/api/products', (req, res) => {
    const categoryFilter = req.query.category;
    let filteredProducts = products;
    if (categoryFilter && categoryFilter !== 'all') {
        filteredProducts = products.filter(p => p.categoryId === categoryFilter);
    }
    res.json(filteredProducts);
});

app.post('/api/products', (req, res) => {
    const { name, price, manufacturer, origin, categoryId } = req.body;
    if (!name || !price || !manufacturer || !origin || !categoryId) {
        return res.status(400).json({ message: 'All fields are required.' });
    }
    const newProduct = {
        id: `p${products.length + 1}`, // Simple ID generation
        name,
        price,
        manufacturer,
        origin,
        categoryId
    };
    products.push(newProduct);
    res.status(201).json(newProduct);
});

app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});