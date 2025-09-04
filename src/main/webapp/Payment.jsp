<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Info - Winning Bidder</title>
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
            width: 120px;
        }

        /* Main Content Section */
        .payment-container {
            display: flex;
            max-width: 1000px;
            width: 100%;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 30px;
            justify-content: space-between;
            flex-wrap: wrap;
        }

        /* Left Column (Bidder Info) */
        .bidder-info {
            flex: 1;
            padding-right: 20px;
            max-width: 500px;
        }

        .bidder-info h3 {
            font-size: 1.6rem;
            margin-bottom: 15px;
            color: #333;
        }

        .bidder-info p {
            font-size: 1rem;
            color: #555;
            margin-bottom: 10px;
        }

        /* Right Column (Payment Info) */
        .payment-info {
            flex: 1;
            padding-left: 20px;
            max-width: 500px;
        }

        .payment-info h3 {
            font-size: 1.6rem;
            margin-bottom: 15px;
            color: #333;
        }

        .payment-info label {
            display: block;
            font-size: 1rem;
            color: #333;
            margin-bottom: 8px;
        }

        .payment-info input {
            width: 100%;
            padding: 12px;
            font-size: 1rem;
            margin-bottom: 20px;
            border: 2px solid #ccc;
            border-radius: 5px;
            outline: none;
            transition: border-color 0.3s;
        }

        .payment-info input:focus {
            border-color: #007bff;
        }

        .submit-btn {
            padding: 12px 20px;
            background-color: #007bff;
            color: white;
            font-size: 1.2rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            width: 100%;
            transition: background-color 0.3s;
        }

        .submit-btn:hover {
            background-color: #0056b3;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .payment-container {
                flex-direction: column;
                align-items: center;
            }

            .bidder-info, .payment-info {
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

    <!-- Payment Info Section -->
    <div class="payment-container">
        <!-- Left Column: Winning Bidder Information -->
        <div class="bidder-info">
            <h3>Winning Bidder Information</h3>
            <p><strong>First Name: </strong>${userInformation.nameFirst}</p>
            <p><strong>Last Name: </strong>${userInformation.nameLast}</p>
            <p><strong>Street Number: </strong>${userAddress.streetNumber}</p>
            <p><strong>Street Name: </strong>${userAddress.streetName}</p>
            <p><strong>City: </strong>${userAddress.city}</p>
            <p><strong>Country: </strong>${userAddress.country}</p>
            <p><strong>Postal Code: </strong>${userAddress.postalCode}</p>
            <p><strong>Total Cost: </strong> $${totalPrice}</p>
        </div>

        <!-- Right Column: Payment Information -->
        <div class="payment-info">
            <h3>Payment Information</h3>
			<form action="payment" method="POST">
            <!-- Card Number -->
            <label for="cardnumber">Credit Card Number</label>
            <input type="text" name="cardnumber" placeholder="1234-5678-9876-5432" required>

            <!-- Name on Card -->
            <label for="cardname">Name on Card</label>
            <input type="text" name="cardname" placeholder="John Doe" required>

            <!-- Expiry Date -->
            <label for="expirydate">Expiry Date</label>
            <input type="month" name="expirydate" required>

            <!-- Security Code (CVV) -->
            <label for="securitycode">Security Code (CVV)</label>
            <input type="text" name="securitycode" placeholder="123" minlength="3" maxlength="3" required>
			<h2>Select Payment Method</h2>
        	<select name="paymentMethod">
            <option value="VISA">Visa</option>
            <option value="MASTERCARD">MasterCard</option>
            <option value="AMEX">American Express</option>
        	</select>
            <!-- Submit Button -->
            <button class="submit-btn" id="submitpayment">Submit Payment</button>
            </form>
        </div>
    </div>

  
</body>
</html>