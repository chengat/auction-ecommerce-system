<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Receipt and Shipping Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        /* Logo */
        .logo {
            margin-bottom: 30px;
        }

        .logo img {
            width: 120px; /* Adjust the logo size as needed */
        }

        /* Main Content Section */
        .receipt-container {
            display: flex;
            justify-content: space-between;
            max-width: 1000px;
            width: 100%;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 20px;
        }

        /* Left Column: Receipt Info */
        .receipt-info {
            flex: 1;
            padding-right: 20px;
            max-width: 500px;
        }

        .receipt-info h3 {
            font-size: 1.6rem;
            margin-bottom: 15px;
            color: #333;
        }

        .receipt-info p {
            font-size: 1rem;
            color: #555;
            margin-bottom: 10px;
        }

        /* Right Column: Shipping Details */
        .shipping-info {
            flex: 1;
            padding-left: 20px;
            max-width: 500px;
        }

        .shipping-info h3 {
            font-size: 1.6rem;
            margin-bottom: 15px;
            color: #333;
        }

        .shipping-info .shipping-date {
            font-size: 2rem;
            font-weight: bold;
            color: #28a745; /* Green color for shipping date */
            margin-top: 30px;
        }

        /* Home Button */
        .home-btn {
            padding: 12px 20px;
            background-color: #007bff;
            color: white;
            font-size: 1.2rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-align: center;
            width: 100%;
            max-width: 200px;
            transition: background-color 0.3s;
            margin-top: 30px;
        }

        .home-btn:hover {
            background-color: #0056b3;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .receipt-container {
                flex-direction: column;
                align-items: center;
            }

            .receipt-info, .shipping-info {
                padding-right: 0;
                padding-left: 0;
                width: 100%;
            }
        }
    </style>
</head>
<body>

    <!-- Logo -->
    <div class="logo">
        <img src="your-logo.png" alt="Logo">
    </div>

    <!-- Receipt and Shipping Info Section -->
    <div class="receipt-container">
        <!-- Left Column: Receipt Info -->
        <div class="receipt-info">
            <h3>Receipt Information</h3>
            <p><strong>First Name: </strong>${userInformation.nameFirst}</p>
            <p><strong>Last Name: </strong>${userInformation.nameLast}</p>
            <p><strong>Street Number: </strong>${userAddress.streetNumber}</p>
            <p><strong>Street Name: </strong>${userAddress.streetName}</p>
            <p><strong>City: </strong>${userAddress.city}</p>
            <p><strong>Country: </strong>${userAddress.country}</p>
            <p><strong>Postal Code: </strong>${userAddress.postalCode}</p>
            <p><strong>Total Paid:</strong> $${totalPrice}</p>
            <p><strong>Item ID:</strong> ${itemId}</p>
        </div>

        <!-- Right Column: Shipping Info -->
        <!-- Right Column: Shipping Info -->
		<div class="shipping-info">
    		<h3>Shipping Details</h3>
    		<p>Your item will be shipped in <strong>${shipdays}</strong> days.</p>
			</div>
        </div>
    </div>

    <!-- Home Button -->
    <button class="home-btn" onclick="window.location.href='welcome';">Home</button>

</body>
</html>