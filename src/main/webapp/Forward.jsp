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

        .bid-input {
            width: 100%;
            padding: 12px;
            font-size: 1.2rem;
            margin-top: 15px;
            border: 2px solid #ccc;
            border-radius: 5px;
            outline: none;
            transition: border-color 0.3s;
        }

        .bid-input:focus {
            border-color: #007bff;
        }

        .submit-btn {
            padding: 12px 20px;
            background-color: #007bff;
            color: white;
            font-size: 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 20px;
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
			<p style="color:green;display:none" id="isOwn">Your Bid</p>
            <p"><strong>Time Remaining:</strong><label data-end="${endTime}" id="timer"></label></p>

            <!-- Input Field for Bid -->
			<form method="POST">
				<label for="new-bid">Enter your bid:</label>
				<input type="number" id="new-bid" name="bidAmount" class="bid-input" placeholder="Enter your bid" step="0.01" min="0">
				
				<!-- Submit Bid Button -->
				<button data-bidder = ${bidUsername} data-username = "${username}" class="submit-btn" id="submit-bid">Submit Bid</button>
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
		
		let bidUsername = document.getElementById("submit-bid").getAttribute("data-bidder");
		let username = document.getElementById("submit-bid").getAttribute("data-username");
		if(bidUsername === username){
			document.getElementById("isOwn").style.display = "block";
		}

		ws = new WebSocket(`/project4413/auction/` + auctionId);

        ws.onopen = function() {
			console.log("Connected to the auction with ID: " + auctionId);
        };
			
        ws.onmessage = function(event) {
			var message = JSON.parse(event.data);
			console.log(message);
			let bidUsername = document.getElementById("submit-bid").getAttribute("data-bidder");
			let username = document.getElementById("submit-bid").getAttribute("data-username");
			if(message.type === "bid"){
				let newBidValue = message.bidAmount;
				document.getElementById("currentBidPrice").textContent = "$" + newBidValue;
				if(message.bidder === username){
					document.getElementById("isOwn").style.display = "block";
				}else{
					document.getElementById("isOwn").style.display = "none";
				}
			}else if(message.type === "auctionEnd"){
				if(message.winnerName === username){
					window.location.href = "auctionEnd?auctionId=" + message.auctionId;
				}else{
					alert("You did not win the auction, redirecting to login");
					window.location.href = "welcome";
				}
			}
        };
    </script>

</body>
</html>
