import java.net.*;
import java.io.*;
import java.util.*;


public class HServer extends Thread
{
	private int port;
	private ServerSocket serverSocket;
	public static final int BUFFER_SIZE = 10000;
	public static int p=0;
	
	public static void main( String[] args )
	{
		int[] portArray = new int[100];
		for (int i=0; i < args.length; i++)
		{
			portArray[i] = Integer.parseInt(args[i]);
		}
		
		try
		{       HServer Thread;	
			for (int i=0; i < args.length; i++)
			{       Thread = new HServer(portArray[i]);
				Thread.start();
			}
		}  

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public HServer(int Temp) throws IOException
	{
		port=Temp;
		System.out.println( "Starting server on port: " + port + "\n");
		serverSocket = new ServerSocket( port);
		serverSocket.setSoTimeout(120000);
	}

	public void run()
	{
		while( true )
		{
			try
			{	
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Connected to " + server.getRemoteSocketAddress() + " through the port " + serverSocket.getLocalPort() +"\n");
				ObjectInputStream in= new ObjectInputStream(server.getInputStream());  
				ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
				String cmd=(String) in.readObject();
				String[] Command = cmd.split(" ");
				String method = Command[0];
				String filePath = Command[1];
				String[] splline = filePath.split(".txt");
				String newPath = splline[0]+ "/server" + ;
				String httpVersion = Command[2];
				if ( method.equals("GET")) 
				{
					File file = new File(filePath);
					if ( !(file.isDirectory()) && (file.exists()) )
					{
						out.writeObject("200 OK");						
						System.out.println("Requested object is " + file.getName());
						BufferedReader buffer = new BufferedReader(new FileReader(file));
						String line = null;						
						while ((line = buffer.readLine())!= null) 
						{
							out.writeObject(line);
						}
						
						System.out.println("file contents of " + file.getName() + " sent \n");	
						buffer.close();
					}
					else
					{
						out.writeObject("404 Not Found");
						out.writeObject("File " + file.getName() + " doesn't exist in the server");
						System.out.println("File " + file.getName() + " doesn't exist\n");
					}
				
				}
				else if ( method.equals("PUT")) 
				{
					try
					{
						byte [] buffer = new byte[BUFFER_SIZE]; 						
						Object object = in.readObject(); 
						File file = new File(filePath);
						if ( !(file.isDirectory()) && (file.exists()) )
						{
							System.out.println("File successfully saved\n");
							out.writeObject("200 OK");
							out.writeObject("File saved in server successfully");					
						}
						else
						{
							System.out.println("Error! File not found\n");
							out.writeObject("404 Not Found");
							out.writeObject("File not saved in server");			
						}
						
						FileOutputStream fos = null;
						if (object instanceof String) 
						{  
							fos = new FileOutputStream(filePath);  
						} 
						else 
						{  
							throwException("Error!");  
						}  
	
						Integer bytesRead = 0; 
						do 
						{  
							object = in.readObject();  
							
	
							if (!(object instanceof Integer)) 
							{  
								throwException("Error!");  
							}  
	
							bytesRead = (Integer)object;	
							object = in.readObject(); 	
							if (!(object instanceof byte[])) 
							{  
								throwException("Error!");  
							}  
	
							buffer = (byte[])object;	 
							fos.write(buffer, 0, bytesRead);  
						} while (bytesRead == BUFFER_SIZE);  
					
						fos.close(); 
					}

					catch(Exception e)
					{
						System.out.println("\n");
					}
				}

				else
				{
					System.out.println("Invalid Method Passed\n");
				}				
				
				in.close();
				out.close();				 
				server.close();
				
			}

			catch(SocketTimeoutException s)
			{
				System.out.println("Socket timed out");
				break;
			}

			catch(IOException e)
			{
				e.printStackTrace();
				break;
			}

			catch(Exception e)  
			{
				e.printStackTrace();
				break;
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public static void throwException(String message) throws Exception 
	{  
		throw new Exception(message);  
	}  
  
	
	
}

