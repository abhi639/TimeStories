package app.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class GetStories
 */
@WebServlet("/")
public class GetStories extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public GetStories() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out=response.getWriter();
		String htmlContent = fetchHTMLContent("https://time.com");
        if (htmlContent != null) {
            List<Story> stories = extractStories(htmlContent);
            String jsonResponse = createJSONResponse(stories);
            System.out.println(stories);
            out.print(jsonResponse);
            // Now you can implement the logic to serve this JSON response via a web service at apiUrl
        } else {
            System.out.println("Failed to fetch HTML content from Time.com");
        }
	
	
	
	}
	private static String fetchHTMLContent(String urlString) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    private static List<Story> extractStories(String htmlContent) {
        List<Story> stories = new ArrayList<>();
        // Regular expression pattern to match story titles and links
        Pattern pattern = Pattern.compile("<li class=\"latest-stories__item\">\\s*<a href=\"(.*?)\">\\s*<h3 class=\"latest-stories__item-headline\">(.*?)</h3>");
        Matcher matcher = pattern.matcher(htmlContent);
        int count = 0;
        while (matcher.find() && count < 6) {
            String link = "https://time.com/"+matcher.group(1);
            String title = matcher.group(2);
            if (!link.isEmpty() && !title.isEmpty()) {
                stories.add(new Story(title, link));
                count++;
            }
        }
        return stories;
    }

    private static String createJSONResponse(List<Story> stories) {
        JSONArray jsonArray = new JSONArray();
        for (Story story : stories) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", story.getTitle());
            jsonObject.put("link", story.getLink());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    static class Story {
        private String title;
        private String link;

        public Story(String title, String link) {
            this.title = title;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out=response.getWriter();
		

		doGet(request, response);
	}

}
