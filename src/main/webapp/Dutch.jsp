<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Item Auction Details</title>
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
            width: 120px; /* Adjust based on your logo size */
        }

        /* Main Content Container */
        .item-container {
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

        /* Left Column (Image) */
        .item-image {
            flex: 1;
            padding-right: 20px;
            text-align: center;
        }

        .item-image img {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
        }

        /* Right Column (Details) */
        .item-details {
            flex: 2;
            padding-left: 20px;
            max-width: 500px;
        }

        .item-details h2 {
            color: #333;
            font-size: 1.8rem;
            margin-bottom: 15px;
        }

        .item-details p {
            font-size: 1rem;
            color: #555;
            margin-bottom: 10px;
        }

        .price-details {
            font-size: 1.2rem;
            color: #333;
            margin-top: 20px;
        }

        .buy-now-btn {
            padding: 12px 20px;
            background-color: #28a745;
            color: white;
            font-size: 1.2rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 20px;
            transition: background-color 0.3s;
            width: 100%;
        }

        .buy-now-btn:hover {
            background-color: #218838;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .item-container {
                flex-direction: column;
                align-items: center;
            }

            .item-image {
                padding-right: 0;
                margin-bottom: 20px;
            }

            .item-details {
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

    <!-- Item Information Section -->
    <div class="item-container">
        <!-- Left Column: Item Image -->
        <div class="item-image">
            <img src="item-image.jpg" alt="Item Image">
        </div>

        <!-- Right Column: Item Details -->
        <div class="item-details">
            <h2>${item.itemName}</h2>
            <p><strong>Description:</strong> ${item.itemDescription}</p>
            <p><strong>Shipping Price:</strong> $${item.shippingCost}</p>
            <p><strong>Current Price:</strong> <label id="currentBidPrice">$${bidPrice}<label></p>
            
            <div class="price-details">
            	<p"><strong>Time Remaining:</strong><label data-end="${endTime}" id="timer"></label></p>
            </div>

            <!-- Buy Now Button -->
            <form method="POST">
            	<button class="buy-now-btn" id="buy-now" data-username = "${username}">Buy Now</button>
           	</form>
        </div>
    </div>

	<script>
    	let timer = document.getElementById("timer");
		function updateEndTime() {
			var currentTime = Date.now();
			var end = parseInt(timer.getAttribute("data-end"));
			if(currentTime > end){
				timer.textContent = "Auction Ended";
			}else{
				//Update Time
				var timeLeft = Math.floor((end - currentTime) / 1000);//Convert to seconds
				let days = Math.floor(timeLeft / (24*60*60));
				timeLeft = timeLeft - (days *24*60*60);
				let hours = Math.floor(timeLeft / (60*60));
				timeLeft = timeLeft - (hours * 60 * 60);
				let minutes = Math.floor(timeLeft / 60);
				timeLeft = timeLeft - (minutes * 60);
				let seconds = timeLeft;
				timer.textContent = days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
			}
		}
		// Call loopOverElements every 1 second (1000ms)
		setInterval(updateEndTime, 1000);
		const urlParams = new URLSearchParams(window.location.search);
		var auctionId = urlParams.get("auctionId");

		ws = new WebSocket(`/project4413/auction/` + auctionId);

        ws.onopen = function() {
			console.log("Connected to the auction with ID: " + auctionId);
        };
			
        ws.onmessage = function(event) {
			var message = JSON.parse(event.data);
			console.log(message);
			let username = document.getElementById("buy-now").getAttribute("data-username");
			if(message.type === "auctionEnd"){
				if(message.winnerName === username){
					window.location.href = "auctionEnd?auctionId=" + message.auctionId;
				}else{
					window.location.href = "welcome";
				}
			}else if(message.type === "priceDecrement"){
				document.getElementById("currentBidPrice").textContent = "$" + message.newPrice;
			}
        };
    </script>

</body>
</html>
