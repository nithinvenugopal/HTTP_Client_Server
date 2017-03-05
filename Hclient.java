import java.io.*;
import java.net.*;
import java.util.*;

public class Hclient 
{
	public static void main( String[] args ) throws Exception
	{
		String Hostname = args[0];
		int port = Integer.parseInt(args[1]);
		String command = args[2];
		String filename = args[3];
		int filesize=6022386 ;
		try
		{   	
			Socket socket = new Socket(Hostname, port);    
			System.out.println("\n" + "Connected to Server with port " + port);
				
			ObjectOutputStream OUT = new ObjectOutputStream(socket.getOutputStream());   
			ObjectInputStream IN = new ObjectInputStream(socket.getInputStream());                      
		
			if (command.equals("GET")) 
			{
				OUT.writeObject(command + " " + filename + " HTTP/1.1");
				OUT.writeObject("Host:" + Hostname);
				String status = (String) IN.readObject();
				System.out.println("Status: " + status + "\n");
				
				if ( status.equals("200 OK")) 
				{
					System.out.println("Requested object content is: \n");
		
					try
					{
						String line = (String) IN.readObject();
						while (line != null) 
						{
							System.out.println(line);
							line = (String) IN.readObject();
						}
					}
					
					catch(EOFException e)
					{
						System.out.println("\nFile received completely\n");
					}
				}
				
				else if ( status.equals("404 Not Found")) 
				{
					System.out.println("File Not Found\n");
				}
				
			}

			else if ( command.equals("PUT")) 
			{
				File file = new File(filename); 
				if (!(file.isDirectory()) && (file.exists()))
				{
					OUT.writeObject(command + " " + filename + " HTTP/1.1");
					System.out.println("Sending the file: " + file.getName() + " ....\n");
					OUT.writeObject(file.getName());  
		
					FileInputStream fis = new FileInputStream(file);  
					byte [] buffer = new byte[filesize];
					Integer bytesRead = 0;  

					bytesRead = fis.read(buffer);
					try{
					if(bytesRead > 0)
					{  
						
						OUT.writeObject(bytesRead);  
						OUT.writeObject(Arrays.copyOf(buffer, buffer.length));  
					}  
					}
					catch(Exception e)
					{
						System.out.println("\n");
					}
					
					System.out.println("File: " + file.getName() + " Sent\n\n");
					
					System.out.println("Status from server: " + IN.readObject() + "\n");
					System.out.println("Status Message from server: " + IN.readObject() + "\n");
				}
				
				else
				{
					System.out.println("File doesn't exist\n");
					OUT.writeObject("Invalid");
				}
			}
			
			else
			{
				System.out.println("Invalid HTTP\n");
				OUT.writeObject("Invalid");
			}
			
			System.out.println("Closing the Socket");
			
			OUT.close();
			IN.close();			
			socket.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
		
	
			
}		
	

