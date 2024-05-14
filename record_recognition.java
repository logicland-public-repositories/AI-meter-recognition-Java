import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class record_recognition {
    String url;
    String login;
    String password;
    String image_path;

    record_recognition(String url, String login, String password, String image_path){
        this.url = url;
        this.login = login;
        this.password = password;
        this.image_path = image_path;
    }

    public String get_results() throws IOError{
        String result = "";
        try{
        HttpURLConnection conn =(HttpURLConnection) new URL(this.url).openConnection();

        File f = new File(this.image_path);
        String boundary_string = UUID.randomUUID().toString();
        String auth = "{"+"\"" + this.login + "\"" + ": " + "\"" + this.password + "\"" + "}";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary_string);
        conn.addRequestProperty("Authorization", new String(encodedAuth));

        OutputStream conn_out = conn.getOutputStream();
        BufferedWriter conn_out_writer = new BufferedWriter(new OutputStreamWriter(conn_out));
        conn_out_writer.write("\r\n--" + boundary_string + "\r\n");
        conn_out_writer.write("Content-Disposition: form-data; " +
                "name=\"image\"; " +
                "filename=\""+ f.getName() +"\"" +
                "\r\n\r\n");
        conn_out_writer.flush();
        
        FileInputStream file_stream = new FileInputStream(f);
        int read_bytes;
        byte[] buffer = new byte[1024];
        while((read_bytes = file_stream.read(buffer)) != -1) {
        conn_out.write(buffer, 0, read_bytes);
        }
        conn_out.flush();
        conn_out_writer.write("\r\n--" + boundary_string + "--\r\n");
        conn_out_writer.flush();

        conn_out_writer.close();
        conn_out.close();
        file_stream.close();
        
        int resp_code = conn.getResponseCode();
        if(resp_code != 200){
            IOException ex = new IOException("Server returned an error \"" + conn.getResponseMessage() +
            "\" with status code " + resp_code);
            throw new IOError(ex);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        result = sb.toString();
    }
    catch(Exception e){
        e.printStackTrace();
    }
    return result;
    }
}
