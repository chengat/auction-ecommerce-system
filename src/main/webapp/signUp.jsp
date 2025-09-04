<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up</title>
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

        /* Sign-Up Form */
        .form-container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 500px;
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
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
        }

        .input-field:focus {
            border-color: #007bff;
            outline: none;
        }

        .input-container {
            display: flex;
            justify-content: space-between;
            gap: 15px;
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

    <!-- Sign-Up Form -->
    <div class="form-container">
        <h1>Create Account</h1>
        
        <form action="signUp" method="POST">
            <!-- First and Last Name -->
            <div class="input-container">
                <input type="text" name="first-name" class="input-field" placeholder="First Name" required>
                <input type="text" name="last-name" class="input-field" placeholder="Last Name" required>
            </div>
            
            <!-- Street Address -->
            <div class="input-container">
                <input type="text" name="street-number" class="input-field" placeholder="Street Number" required>
                <input type="text" name="street-name" class="input-field" placeholder="Street Name" required>
            </div>
            
            <!-- City, Postal Code, Country -->
            <div class="input-container">
                <input type="text" name="city" class="input-field" placeholder="City" required>
                <input type="text" name="postal-code" maxlength="6" minlength="6" class="input-field" placeholder="Postal Code" required>
                <input type="text" name="country" class="input-field" placeholder="Country" required>
            </div>

            <!-- Username and Password -->
            <input type="text" name="username" minlength="5" class="input-field" placeholder="Username" required>
            <input type="password" name="password" minlength="5" class="input-field" placeholder="Password" required>
            
            <!-- Sign Up Button -->
            <button type="submit" class="submit-btn">Sign Up</button>
            <p>${error}</p>
        </form>
    </div>
</body>
</html>
