package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import com.google.thoughtcrimegson.Gson;
import com.google.thoughtcrimegson.JsonObject;
import com.google.thoughtcrimegson.JsonParser;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String inputData = request.getParameter("userInput");
		
		//api setup
		String apiKey = "9952761efa2ff5caab21c73a7de673f4";
		
		//get the city from the input
		String city = request.getParameter("city");
		
		//create the url for the openWeatherapp api request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+ apiKey ;
		
		//API integration
		URL url = new URL(apiUrl);
		
		 HttpsURLConnection connection = null;
		
		 try {
			    connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				
				//Reading the data from network
				InputStream inputStream = connection.getInputStream();
				InputStreamReader reader = new InputStreamReader(inputStream);
				
				//want to store in string 
				StringBuilder responseContent = new StringBuilder();
				
				//input lene ke liye froom the reader will create the scanner
				Scanner scanner = new Scanner(reader);
				while(scanner.hasNext()) {
					responseContent.append(scanner.nextLine());
				}
				scanner.close();

				//Typecasting == parsing the data into json
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class );
//				System.out.println(jsonObject);
				
				//time and date
				// Convert the Unix timestamp to milliseconds and create a Date object
			      
				
				 long dataTimestamp = jsonObject.get("dt").getAsLong() * 1000;
			     Date date = new Date(dataTimestamp);

			    // Format the date and time using SimpleDateFormat
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    String formattedDate = sdf.format(date);

			    //Print the formatted date and time
//			    System.out.println(formattedDate);

		       
				//Tempareture
				double temparetureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
				int temparatureCelsius = (int) (temparetureKelvin -273.15);
				
				//Humidiity
				int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
				
				//wind speed 
				double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
				
				//weather condition
				String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
				
				//set the data as request attribute (for sending to the jsp page)
				
				request.setAttribute("formattedDate", formattedDate);
				request.setAttribute("city", city);
				request.setAttribute("temparatureCelsius", temparatureCelsius);
				request.setAttribute("weatherCondition", weatherCondition);
				request.setAttribute("humidity", humidity);
				request.setAttribute("windSpeed", windSpeed);
				request.setAttribute("weatherData", responseContent.toString());
			 
		 }
		 catch (IOException e) 
		 {
		        // Handle connection errors gracefully
		        // You can set default values or display an error message
		        request.setAttribute("error", " Unable to connect to weather service deu to internet conectivity.");
		        e.printStackTrace(); // Print or log the exception for debugging
		 }
		 finally {
		        if (connection != null) {
		            connection.disconnect();
		        }
		    }
		
		connection.disconnect();
		
		//forward the request to the weather.jsp page for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
}
