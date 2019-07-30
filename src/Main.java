import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Main {

	final static String ip = "192.168.0.103";

	public static void main(String[] args) {
		try {
			System.out.println("Programa iniciado");

			long contador = 0;
			long limite = 100;
			int tiempoDormir = 5000; // Milisegundos a esperar entre cada iteración
			String rutaCarpeta = "/Users/DANIEL/Desktop/Prueba";

			while (contador++ < limite) { // Seguro
				System.out.println("Comienza a buscar archivos\n" + contador + "/" + limite);

				procesarArchivos(new File(rutaCarpeta));

				System.out.println("Durmiendo");

				TimeUnit.MILLISECONDS.sleep(tiempoDormir);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void procesarArchivos(final File carpeta) {
		for (final File archivo : carpeta.listFiles()) {
			if (!archivo.isDirectory()) {
				System.out.println("Encontrado archivo:");
				String nombreArchivo = carpeta.getAbsolutePath() + "\\" + archivo.getName();
				System.out.println(nombreArchivo);

				// This will reference one line at a time
				String contenido = "";

				try {
					// FileReader reads text files in the default encoding.
					FileReader fileReader = new FileReader(nombreArchivo);

					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String line = bufferedReader.readLine();
					while (line != null) {
						contenido += line + "\n";
						line = bufferedReader.readLine();
					}
					// System.out.println(contenido);
					System.out.println("Enviando con POST");
					hacerPOST(contenido);

					// Always close files.
					bufferedReader.close();

					// Eliminar el archivo

					if (archivo.delete()) {
						System.out.println(archivo.getAbsolutePath() + " se ha eliminado");
					} else
						System.out.println(archivo.getAbsolutePath() + " no se ha encontrado en la carpeta");

				} catch (FileNotFoundException ex) {
					System.out.println("Incapaz de abrir el archivo '" + nombreArchivo + "'");
					ex.printStackTrace();
				} catch (IOException ex) {
					System.out.println("Error leyendo el archivo, o incapaz de eliminar '" + nombreArchivo + "'");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public static void hacerPOST(String contenido) throws Exception {
		final URL url = new URL("http://" + ip + "/redes/index.php");
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST"); // PUT is another valid option
		http.setDoOutput(true);

		Map<String, String> argumentos = new HashMap<>();
		argumentos.put("foto", contenido);
		StringJoiner sj = new StringJoiner("&");
		for (Map.Entry<String, String> entry : argumentos.entrySet())
			sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
		byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
		int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		http.connect();
		try (OutputStream os = http.getOutputStream()) {
			os.write(out);
		}

	}

}
