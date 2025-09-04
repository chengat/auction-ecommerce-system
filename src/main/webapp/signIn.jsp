<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In</title>
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
            margin-bottom: 20px;
        }

        .logo img {
            width: 120px; /* Adjust based on your logo size */
        }

        /* Sign-In Form */
        .form-container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
        }

        h1 {
            font-size: 2rem;
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        .input-field {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
        }

        .input-field:focus {
            border-color: #007bff;
            outline: none;
        }

        /* Button Styles */
        .submit-btn {
            display: block;
            width: 100%;
            padding: 14px;
            background-color: #007bff;
            color: white;
            font-size: 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .submit-btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <!-- Logo -->
    <div class="logo">
        <img src="your-logo.png" alt="Logo">
    </div>

    <!-- Sign-In Form -->
    <div class="form-container">
        <h1>Sign In</h1>
        
        <form action="signIn" method="POST">
            <!-- Username Input -->
            <input type="text" name="username" class="input-field" placeholder="Username" required>
            
            <!-- Password Input -->
            <input type="password" name="password" class="input-field" placeholder="Password" required>
            
            <!-- Sign In Button -->
            <button type="submit" class="submit-btn">Sign In</button>
            <p>${error}</p>
        </form>
    </div>
</body>
</html>
