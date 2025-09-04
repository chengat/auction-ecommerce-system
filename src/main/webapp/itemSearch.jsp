<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        /* Logo */
        .logo {
            margin-bottom: 30px;
        }

        .logo img {
            width: 120px; /* Adjust based on your logo size */
        }

        /* Search Bar */
        .search-container {
            width: 100%;
            max-width: 500px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .search-input {
            width: 100%;
            padding: 12px;
            font-size: 1.2rem;
            border: 2px solid #ccc;
            border-radius: 5px;
            outline: none;
            transition: border-color 0.3s;
        }

        .search-input:focus {
            border-color: #007bff;
        }

        .search-button {
            padding: 12px 20px;
            background-color: #007bff;
            color: white;
            font-size: 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-left: 10px;
            transition: background-color 0.3s;
        }

        .search-button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

    <!-- Logo -->
    <div class="logo">
        <img src="your-logo.png" alt="Logo">
    </div>

    <!-- Search Bar Section -->
            <form action="itemSearch" method="POST">
    <div class="search-container">
        <input name="search" type="text" class="search-input" placeholder="Search..." aria-label="Search">
        <button class="search-button">Search</button>
    </div>
    </form>

</body>
</html>
