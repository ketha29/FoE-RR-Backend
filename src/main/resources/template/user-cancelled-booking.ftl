<html>
    <body>
        <h2>FOE Room Reservation</h2>

        Dear Admin,<br>
        <p>This is to inform you that a booking has been successfully cancelled by the user</p>

        <h3>Cancelled Booking : details</h3>
        <div><b>
            <table>
                <tr>
                    <td style="padding: 3px">Username</td>
                    <td style="border-left: 2px solid red; padding: 5px;"> ${userName}</td>
                </tr>
                <tr>
                    <td style="padding: 3px">Booked room</td>
                    <td style="border-left: 2px solid red; padding: 5px;"> ${roomName}</td>
                </tr>
                <tr>
                    <td style="padding: 3px">Cancelled days</td>
                    <td style="border-left: 2px solid red">
                    	<ul style="list-style-type: none; padding: 0; margin: 0;">
                    		<#list dates as date>
                    			<li style="margin: 5px;">${date}</li>
                    		</#list>
                    	</ul>
                    </td>
                </tr>
                <tr>
                    <td style="padding: 3px">From time</td>
                    <td style="border-left: 2px solid red; padding: 5px;"> ${startTime}</td>
                </tr>
                <tr>
                    <td style="padding: 3px">To time</td>
                    <td style="border-left: 2px solid red; padding: 5px;"> ${endTime}</td>
                </tr>
            </table>
        </div></b>

        <br>Thankyou.
    </body>
</html>