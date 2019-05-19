package fds.java.queue;


import com.zink.queue.Connection;
import com.zink.queue.ConnectionFactory;
import com.zink.queue.WriteChannel;

import java.io.FileInputStream;
import java.util.Scanner;


/**
 * Stub for the Producer of information to queue
 * <p>
 * From Fly Docker
 * <p>
 * > docker run -p 4396:4396 zink/fly
 * <p>
 * To check it is running
 * > docker ps
 */
public class Producer {

    public static void main(String[] args) throws Exception {

        final String ipAddr = "192.168.1.84";
        Connection con = ConnectionFactory.connect(ipAddr);
        WriteChannel wc = con.publish("BBC7");

        wc.write("Hello Consumer");

        /* Read a file from the local file system line by line */
        final String fileName = "file.log";
        FileInputStream fis = new FileInputStream(fileName);
        Scanner scanner = new Scanner(fis);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // TODO
            // Write to channel until the file is finished
        }
        // TODO
        // Write an end of stream marker
    }

}
