import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.activation.MimetypesFileTypeMap;

public class JavaWebServer {

	private static final int NUMBER_OF_THREADS = 100;
	private static final Executor THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);

		// Waits for a connection request
		while (true) {
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					HandleRequest(connection);
				}
			};
			THREAD_POOL.execute(task);

		}

	}

	private static void HandleRequest(Socket s) {
		BufferedReader in;
		PrintWriter out;
		String request;
		String fileName = "";
		String fileType = "";
		String htmlCode = "";
		
		try {
			String webServerAddress = s.getInetAddress().toString();
			System.out.println("New Connection:" + webServerAddress);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			while (in.ready()) {
				request = in.readLine();
				System.out.println("--- Client request: " + request);

				if (request.startsWith("GET /")) {
					for (int i = 5; i < request.length() - 9; i++) {
						fileName += request.charAt(i);
					}
				}
				System.out.println(fileName);
			}

			out = new PrintWriter(s.getOutputStream(), true);
			out.println("HTTP/1.0 200");
			if (fileName.contains(".js")) {
				out.println("Content-type: application/javascript");
			} else {
				out.println("Content-type: text/html");
			}
			out.println("Server-name: myserver");
			Scanner scanner = new Scanner(new File(fileName));
			// out.println("Content-length: " + response.length());
			out.println("");
			// out.println(response);
			while (scanner.hasNextLine()) {
				htmlCode += scanner.nextLine();
			}
			out.println("Content- length: " + htmlCode.length());
			out.println(htmlCode);
			out.flush();
			out.close();
			s.close();
		} catch (IOException e) {
			System.out.println("Failed respond to client request: " + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

}