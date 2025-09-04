<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            height: 100vh;
            padding: 20px;
        }

        /* Logo */
        .logo {
            margin-bottom: 30px;
        }

        .logo img {
            width: 120px; /* Adjust based on your logo size */
        }

        /* Table Styles */
        .table-container {
            width: 100%;
            max-width: 900px;
            overflow-x: auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #007bff;
            color: white;
        }

        td {
            background-color: #f9f9f9;
        }

        tr:hover td {
            background-color: #f1f1f1;
        }

        .radio-column {
            text-align: center;
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
    <!-- Table Container -->
    <form id="searchform" action="searchResults" method="POST"> 
	    <div class="table-container">
	        <table>
	            <thead>
	                <tr>
	                    <th>Item Name</th>
	                    <th>Current Price</th>
	                    <th>Auction Type</th>
	                    <th>Remaining Time</th>
	                    <th>Select</th>
	                </tr>
	            </thead>
	            <tbody>
	                <c:forEach var="entry" items="${searchResults}">
					    <tr class = "entry" data-type="${entry.value.auction.auctionType}" data-id = "${entry.value.auction.auctionId}">
					        <td>${entry.value.item.itemName}</td>
					        <c:set var="key" value="${entry.key}" />
					        <td class = "price"><%="$"%>${searchPrices[key]}</td>
					        <td>${entry.value.auction.auctionType}</td>
					        <td data-end="${entry.value.auction.auctionClose}" class="timeEnd"></td>
					        <td class="radio-column"><input type="radio" name="item" value="${entry.key}"></td>
					    </tr>
					</c:forEach>
	            </tbody>
	        </table>
	        <!-- Submit Button -->
	        <button id="submit" class="submit-btn" type="submit">Submit Selection</button>
			<script>
				const rows = document.querySelectorAll('.entry');
				var connections = [];
				rows.forEach((element, index) => {
					let connection = new WebSocket(`/project4413/auction/` + element.getAttribute("data-id"));
					connections.push(connection);
					connection.onmessage = function(event) {
						var message = JSON.parse(event.data);
						if(message.type === "bid"){
							element.querySelector(".price").textContent = "$" + message.bidAmount;
						}else if(message.type === "priceDecrement"){
							element.querySelector(".price").textContent = "$" + message.newPrice;
						}else if(message.type === "auctionEnd"){
							element.remove();
						}
					}
				});
				function updateEndTime() {
					const elements = document.querySelectorAll('.timeEnd');
					var currentTime = Date.now();
					elements.forEach((element, index) => {
						var end = parseInt(element.getAttribute("data-end"));
						if(currentTime > end){
							//Delete
							element.parentElement.remove();
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
							element.textContent = days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
						}
					});
				  }
	
			  // Call loopOverElements every 1 second (1000ms)
			  setInterval(updateEndTime, 1000);
			</script>
	    </div>
    </form>

</body>
</html>
