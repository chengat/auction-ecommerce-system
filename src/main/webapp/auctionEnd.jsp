<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bidding Ended - Payment</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        /* Main Content Section */
        .result-container {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 800px;
            width: 100%;
            text-align: left;
        }

        h2 {
            color: #333;
            font-size: 1.8rem;
            margin-bottom: 20px;
        }

        .item-info {
            font-size: 1.2rem;
            color: #555;
            margin-bottom: 20px;
        }

        .question {
            font-size: 1.2rem;
            margin-bottom: 15px;
            color: #333;
        }

        .radio-buttons {
            margin-bottom: 20px;
        }

        .radio-buttons label {
            margin-right: 20px;
        }

        .pay-now-btn {
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

        .pay-now-btn:hover {
            background-color: #0056b3;
        }

        .item-info span {
            font-weight: bold;
        }
    </style>
</head>
<body>

    <!-- Bidding Ended Result -->
    <div class="result-container">
        <h2>Winner!</h2>

        <div class="item-info">
            <h2>${item.itemName}</h2>
            <p><strong>Description:</strong> ${item.itemDescription}</p>
            <p><strong>Shipping Price:</strong> $${item.shippingCost}</p>
        </div>

        <div class="question">
            Would you like to expedite shipping for $${item.expeditedShippingCost} instead?
        </div>

        <!-- Shipping Option Form -->
        <form action="payment" method="GET">
            <div class="radio-buttons">
                <label>
                    <input type="radio" name="ShippingType" value="EXPEDITED">Yes</label>
                <label>
                    <input type="radio" name="ShippingType" value="STANDARD" checked>No</label>
            </div>

            <!-- Pay Now Button -->
            <button type="submit" class="pay-now-btn">Pay Now</button>
        </form>
    </div>

</body>
</html>