package com.gigaspaces.mq.listener;

import com.gigaspaces.mq.spacelistener.SpaceReplicator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MQlistener implements MessageListener {

	public static String queueManager = DB2SpaceListener.queueManager;
	public static String queue = DB2SpaceListener.queue;
	public static long runTime;
	public static String outPath = DB2SpaceListener.outPath;
	public static File outDir = DB2SpaceListener.outDir;
	public static File stageDir = DB2SpaceListener.stageDir;
	public static File batchDir = DB2SpaceListener.batchDir;

	public static String outColDelimHexString = "1E";
	public static String outRecordDelimHexString = "0A";
	public static char outColDelim;
	public static char outRecordDelim;

	public static String inColDelimHexString = "2C";
	public static String inStringDelimHexString = "22";
	public static String inRecordDelimHexString = "0A";
	public static char inColDelim;
	public static char inStringDelim;

	public static int tableNameIndex = 5;
	public static int operationIndex = 6;
	public static int segmentIndex = 11;

	public static boolean stage;
	private static final char CR = '\r';
	private static final char SPACE = ' ';
	private static final Pattern HAS_CHAR = Pattern.compile(".*\\p{Alpha}.*");
	public static final Pattern DATE_PATTERN =
			Pattern.compile("^(\\d){4}(\\D)(\\d){2}(\\D)(\\d){2}(\\D)(\\d)" +
					"{2}(\\D)(\\d){2}(\\D)(\\d){2}(\\D(\\d*))?$");


	public void onMessage(Message message) {
        if (!batchDir.exists() && !stage) {
			batchDir.mkdir();
		}
		try {
			if (message instanceof TextMessage) {
				String messageText = ((TextMessage) message).getText();

				processMessageListener(messageText);

				if (!stage && (stageDir.listFiles().length > 0)) {
					// stage directory not empty & transaction complete
					consolidateFiles();
				}
			} else {
				System.out.println(" Received message of unsupported type.");
			}
		} catch (JMSException je) {
			System.err.println(" ");
			System.err.println(je);
			Exception e = je.getLinkedException();
			if (e != null) {
				System.err.println(e);
			} else {
				System.err.println("DB2SpaceListener.onMessage: No linked exception found.");
			}
		} catch (IOException ioe) {
			System.err.println(" ");
			System.err.println(ioe);
		}
	}

	 /*
	  * processMessageListener(String text) fetch message from queue Send it to SpaceReplicator for write it into the Xap
	  * by calling processMessage() of SpaceRelicator
	  * and also write message in file 
	  */
		public void processMessageListener(String text) throws IOException {
			
			SpaceReplicator spcrepl = new SpaceReplicator(text);
			spcrepl.processMessage();

			System.out.println("MQ Message : "+text);
			String[] lines = text.split("\\x" + inRecordDelimHexString);
			String record;
					
				boolean valid = true;
				int i = 0, j = 1;
				int[] delimCount = new int[lines.length];
				
				List<String> recordList = new ArrayList<String>();
				
				if (lines.length == 1) {
					recordList.add(chop(lines[0],false));
				}
				
				while(i < (lines.length - 1)) {
				  record = lines[i];
				  // validate next record
					valid = validateRecord(lines[j]); 						    
					
					while ((!valid) && (j < lines.length)) { 				  
	          // record invalid - concat to end of current record
						record = chop(record, true) + ' ' + lines[j]; 	 
						j++;
						if (j < lines.length) {
							valid = validateRecord(lines[j]);
						}
					}
					recordList.add(chop(record,false));                 					 
					i = j;
					j++;
				}
				if (lines.length > 1) {                                
				  if (valid) {
				     recordList.add(chop(lines[i],false));
				     }
				}
							
				int recordCount = recordList.size();
				// DEP segment_number
				int segment = getSegment(recordList);               
				if (segment == -1) {
				  System.out.println(" ");
					System.err.println(" processMessage: Can't read column delimiter.");
				  System.out.println("The program is ending."); 
				  System.exit(2);
				}
				File workingDir = null;
				if (segment > 0) {
					workingDir = stageDir;
					stage = true;
				} else {
					if (stage) {
						workingDir = stageDir;
						stage = false;
					} else {
						workingDir = batchDir;
					}
				}
				
				Iterator itr = recordList.iterator();
				String[] values;
				String tableName, out;
				boolean delimStart, delimEnd, inStringFlag = false;
				File outFile = null;
				FileWriter outputStream = null;
				
				while(itr.hasNext()) {                     
				  // for each record in this message
					record = (String)itr.next();
					// values array contains all bef/aft cols of record
	  			values = record.split("\\x" + inColDelimHexString); 
	  			// DEP table_name
					tableName = values[tableNameIndex].substring(1, (values[tableNameIndex].length() - 1));
			
					if (tableName.length() > 0) {								  
						outFile = new File(workingDir + File.separator + tableName + ".dat");
						try {
							outputStream = new FileWriter(outFile, true);
							// DEP operation: ISRT, REPL or DLET 
							writeToStream(outputStream, addOutColDelim(
							   values[operationIndex].substring(1, (values[operationIndex].length() - 1))));
							// DEP data  
							for (int m = 12; m < values.length; m++) {
								delimStart = values[m].startsWith(Character.toString(inStringDelim));
								delimEnd = values[m].endsWith(Character.toString(inStringDelim));
								if (delimStart && delimEnd) {
								  // char value enclosed in string delimiters
								  out = values[m].substring(1, (values[m].length() - 1));
								  if (out.length() > 18 && out.length() < 31) {
										if (checkDate(out)) {
										  out = formatDate(out);
										}
									}
									out = addOutColDelim(out);
								} else if (delimStart){
								  // begin char  
									inStringFlag = true;
									out = addInColDelim(values[m].substring(1, (values[m].length() - 1)));
								} else if (delimEnd) {
								  // end char
				  				inStringFlag = false;
									out = addOutColDelim(values[m].substring(0, (values[m].length() - 1)));
								} else if (inStringFlag) {
								  // middle of char
									out = addInColDelim(values[m]);
								} else {
								  // non-char  
								 	out = addOutColDelim(values[m]);
								}
								if (m == (values.length -1)) {
	               	out = addRecordDelim(out.substring(0, (out.length() - 1)));
	              }
								writeToStream(outputStream, out);
							}
						} finally {
							if (outputStream != null) {
								outputStream.close();
							}
						}
					} else {
		  		   System.out.println(" ");
					   System.err.println(" processMessage: Can't read table name.");
				     System.out.println("The program DB2SpaceListener is ending."); 
				     System.exit(2);
					}
				} // all records processed for this message
			}
		
	//********************************************************************/
	//  Output file formatting functions:                                */
	//   - addInColDelim:       */
	//   - addOutColDelim: */
	//   - addRecordDelim:  */
  //********************************************************************/		
		
		public String addInColDelim(String str) {
			return str.concat(Character.toString(inColDelim));
		}
		
		public String addOutColDelim(String str) {
			return str.concat(Character.toString(outColDelim));
		}
		
		public String addRecordDelim(String str) {
			return str.concat(Character.toString(outRecordDelim));
		}

  //********************************************************************/
	//   getSegment returns the EP segment_ number                       */
	//********************************************************************/		
		public int getSegment(List<String> strList) throws NumberFormatException {
		  
		  String str = (String)strList.get(0);
		  
		  int index = str.indexOf("\\x" + inColDelimHexString);
			if (index == 0) {
			 return -1 ;
			}
		  String[] elms = str.split("\\x" + inColDelimHexString);
			int segment = Integer.parseInt(elms[segmentIndex]);
			return segment;
		}
		
	//********************************************************************/
	// checkDate looks for a possible DB2 Date pattern                   */
	//********************************************************************/		
		public boolean checkDate(String str) {
			Matcher m1 = HAS_CHAR.matcher(str);
			if (m1.matches()) {
				return false;
			}
			Matcher m2 = DATE_PATTERN.matcher(str);
			return m2.matches();
		}

  //********************************************************************/
	// formatDate changes the DB2 date format to a more generic output   */
	// date format. If you want to populate the file with a different    */
	// date output format, you need to update the method below.          */ 
  //********************************************************************/		
		public String formatDate(String str) {
			String ret = str.substring(0, 4).concat("-");				// `YYYY-`
			ret = ret.concat(str.substring(5, 7).concat("-"));	// `MM-`
			ret = ret.concat(str.substring(8, 10).concat(" "));	// `DD `
			ret = ret.concat(str.substring(11, 13).concat(":"));// `HH:`
			ret = ret.concat(str.substring(14, 16).concat(":"));// `MM:`
			ret = ret.concat(str.substring(17, 19));				    // `SS`

			if (str.length() > 20) {
				ret = ret.concat(".");
				ret = ret.concat(str.substring(20));
			}
			return ret;
		}
		
	//********************************************************************/
	// writeToStream is the generic write method that is invoked for     */
	// every column of every record to populate the output file          */
	//********************************************************************/		
		public void writeToStream(FileWriter outputStream, String str) 
		throws IOException {
			for (int i=0; i < str.length(); i++) {
				try {
					outputStream.write(str.charAt(i));
				} catch (IOException ioe) {
				  System.err.println(" ");
				  System.err.println("Error writing:");
					System.err.println(ioe);
					System.out.println(" ");
			    System.out.println("The program is ending."); 
			    System.exit(2);
				}
			}
		}
	
	//********************************************************************/
	// chop removes the last CR character of a string                    */
	//********************************************************************/		
		public String chop(String str, boolean replace) {
			if( str == null ) {
				return null;
			}
			int strLen = str.length();
			if( strLen == 0 ) {
				return "";
			}
			int lastIdx = strLen - 1;
			char last = str.charAt(lastIdx);
			if( last == CR ) {
				if( replace ) {
					return str.substring(0, lastIdx).concat(Character.toString(SPACE));
				} else {
					return str.substring(0, lastIdx);
				}
			}
			return str;
		}

  //********************************************************************/
	// validateRecord validates (the existence of) column delimiters     */
	// and validates that the first entry of record is EP type (integer  */
	// value in the message header)                                      */
	//********************************************************************/		
		public boolean validateRecord(String str) {
			int index = str.indexOf(inColDelim);
			if (index > 0) {
				try {
					Integer.parseInt(str.substring(0, index));
					return true;
				} catch(NumberFormatException nfe) {
					return false;
				}
			}
			return false;
		}

  //********************************************************************/
	// consolidateFiles merges the contents of the stage directory into  */
	// the batch directory                                               */
	//********************************************************************/		
		public void consolidateFiles() throws IOException {
			if (!batchDir.exists()) {
				batchDir.mkdir();
			}
			
			File[] sourceList = stageDir.listFiles();
			File target = null;
			for (int i=0; i<sourceList.length; i++) {
			
				target = new File(batchDir + File.separator + sourceList[i].getName());
				if (target.exists()) {
				
					BufferedReader inputStream = null;
					PrintWriter outputStream = null;
					try {
						inputStream = new BufferedReader(new FileReader(sourceList[i].getAbsolutePath()));
						outputStream = new PrintWriter( new FileWriter(target, true));

						String str;
						while ((str = inputStream.readLine()) != null) {
							outputStream.println(str);
						}
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						if (outputStream != null) {
							outputStream.close();
						}
						boolean success = sourceList[i].delete() ;
						if (!success) {
						  System.out.println(" consolidateFiles: Target exists but can't be deleted");
  					}
					}		
				} else {
				  sourceList[i].renameTo(target);
				}
			}
		}
	}