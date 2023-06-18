/*
- COP 3330 Final Project
- Diego La Rosa, Jean Carlo Grimaldo, Shaoyan Zhai 
*/

import java.awt.datatransfer.SystemFlavorMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FinalProject {
	public static Scanner scanInput = new Scanner(System.in);
	
	//Method used to get a valid ID for the TA and Faculty members
	static int getValidId(boolean isValid) {
		int id = 0;
		while(!isValid) {
			try {
				id = (scanInput.nextInt());
				if( id < 1000000 || id > 9999999 ) 
				{
					throw new IdException(id);
				}			
				isValid = true;
			} 
			catch(IdException e) 
			{
				System.out.println(e.getMessage());
				System.out.println("Try again");
			} 
			catch(Exception e) 
			{
				System.out.println("Please enter 7 digits");
				scanInput.next();
			}
		}
		return id;
	}
	
	static UcfMember findMember(ArrayList <UcfMember> members, int id) {
		for(UcfMember member : members) {
			if(id == member.getId()) {
				return member;
			}
		}
		return null;
	}
	
	static boolean idFound(ArrayList <UcfMember> members, int id) {
		for(UcfMember member : members) {
			if(id == member.getId()) {
				return true;
			}
		}
		return false;
	}
	
	//Used to check that the user entered specific inputs for the degree seeking or the student type
	static String validInput(String case1, String case2) {
		String output;
		output = scanInput.next();
		while(!output.equalsIgnoreCase(case1) && !output.equalsIgnoreCase(case2))
		{
			System.out.println("Sorry, but " + output + " is not a degree.");
			System.out.println("Valid degrees are: " + case1 + " or " + case2 + "\nPlease try again"); 
			output = scanInput.next();
		}
		return output;
	}
	
	static String [] requestTAInfo(String taSupervisor, String degreeSeeking) {
		String [] TAInfo = new String [2];
		System.out.println("TA’s supervisor’s name: ");
		taSupervisor =  (new Scanner(System.in)).nextLine();
		System.out.println("Degree Seeking");
		degreeSeeking = validInput("MS", "PhD");
		TAInfo[0] = taSupervisor;
		TAInfo[1] = degreeSeeking;
		return TAInfo;
	}
	
	// Used to ask the user their id input and check if it is on the arrayList for options 3,4, and 5
	static UcfMember startPrintOption(ArrayList <UcfMember> members, String memberType) {
		System.out.println("Enter UCF id: ");
		int id = getValidId(false);
		UcfMember member = findMember(members, id);
		if(memberType.equalsIgnoreCase("Faculty")) {
			if(member == null || member instanceof Faculty == false ) {
				System.out.println("No faculty member was found.");
				return null;
			} 
		} else if(memberType.equalsIgnoreCase("TA")) {
			if(member == null || member instanceof TA == false ) {
				System.out.println("No TA member was found.");
				return null;
			} 
		} else if(memberType.equalsIgnoreCase("Student")){
			if(member == null || member instanceof Student == false ) {
				System.out.println("No student member was found.");
				return null;
			} 
		}	
		return member;
	}
	
	// Used to print the schedules of Faculties and student members since they are very similar in output
	static void printSchedule(UcfMember member) {
		int studentIndex = 0;
		int facultyIndex = 0;
		for(String classInfo : member.getClasses()) {
			String[] organizeDisplay = classInfo.split(",");
			if(organizeDisplay[4].equalsIgnoreCase("online")) {
				System.out.println("\t[" + organizeDisplay[1] + "/" + organizeDisplay[2] +"]" + " [" + organizeDisplay[4] + "]");
			}
			else {
				if(organizeDisplay[6].equalsIgnoreCase("yes")) {
					if(member instanceof Faculty) {
						try {
							System.out.println("\t[" + organizeDisplay[0] + "/" + organizeDisplay[1] + "/" +organizeDisplay[2] +"]" + "with Labs:");	
							System.out.println("\t\t["+member.getLabs().get(facultyIndex) + "]");
							System.out.println("\t\t["+member.getLabs().get(facultyIndex + 1) + "]");
							System.out.println("\t\t["+member.getLabs().get(facultyIndex + 2) + "]");
							facultyIndex += 3;							
						}catch(Exception e) {
							System.out.println("There are more or less than 3 labs for this class");
						}
					}else if(member instanceof Student){
						System.out.print("\t[" + organizeDisplay[0] + "/" + organizeDisplay[1] + "/" +organizeDisplay[2] +"]" + "/[Lab: ");
						System.out.println(member.getLabs().get(studentIndex) + "]");
						studentIndex++;
					}
				}
				else
				{
					System.out.println("\t[" + organizeDisplay[0] + "/" + organizeDisplay[1] + "/" +organizeDisplay[2] +"]");
				}
			}
			}
	}
	static void deleteLectureFromArray(ArrayList<String> toDelete, String delete) {
		for(String crn : toDelete) {
			if(crn.contains(delete)) {
				delete = crn;
			}
		}
		if(delete != null) toDelete.remove(delete);
		delete = null;
	}
	
	static void deleteLabFromArray(ArrayList<String> toDelete, String labs, int index) {
		String [] labsFound = labs.split("\\*");
		String delete = null;
		for(String deleteLab : toDelete) {
			if(labsFound[index].equalsIgnoreCase(deleteLab)) {
				delete = deleteLab;
			}
		}
		toDelete.remove(delete);
	}
	
	static void deleteFromFile(String path,String delete) {
	    try {
	    File file = new File(path);
	    FileInputStream fis = new FileInputStream(file);

	    byte[] data = new byte[(int) file.length()];
	    fis.read(data);
	    fis.close();

	    String fileContents = new String(data, "UTF-8");
	    fileContents = fileContents.replace(delete, "").replace("\r", "");
	    
	    FileOutputStream fos = new FileOutputStream(file);
	    fos.write(fileContents.getBytes());
	    fos.close();}
	    catch(Exception e){}
	    
	}
	public static void main(String args[]) {
		
		Scanner scanFile = null;
		String scannerPath = "";
		ArrayList <String> lectureAndLabs;
		lectureAndLabs = new ArrayList<String>();
		int lectureAndLabsIndex = 0;
		boolean fileUpdated = false;
		
		//DATA STRUCTURE THAT STURE ALL MEMBERS
		ArrayList <UcfMember> members;
		members = new ArrayList<UcfMember>();
		//-----------------------------------
		int option = 0;
		
		// Entering the correct path file
		//--------------------------------------------------------
		System.out.println("Enter the absolute path of the file:");
		boolean correctPath = false;
		while(!correctPath) {
			try {
				scannerPath = scanInput.nextLine();
				scanFile= new Scanner(new File (scannerPath));
				correctPath = true;
			}
			catch(Exception e) {
					System.out.println("Sorry no such file.\nTry again:");
			}
		}
		System.out.println("File Found! Let's proceed...");
		//--------------------------------------------------------
				
		while( option != 7) {
			// Making the main menu
			//--------------------------------------------------------
			System.out.println("*****************************************");
			System.out.println("Choose one of these options:");
			System.out.println("\t1- Add a new Faculty to the schedule");
			System.out.println("\t2- Enroll a Student to a Lecture");
			System.out.println("\t3- Print the schedule of a Faculty");
			System.out.println("\t4- Print the schedule of an TA");
			System.out.println("\t5- Print the schedule of a Student");
			System.out.println("\t6- Delete a Lecture");
			System.out.println("\t7- Exit");
			System.out.print("\t\tEnter your choice:");
			//--------------------------------------------------------
			
			 //Check the user entered an integer AND a valid option
			//----------------------------------------------------
			try {
				 option = scanInput.nextInt();
			} 
			catch(Exception e) {
				System.out.println("Sorry, but that is not an option, please try again");
				scanInput.next();
				continue;
			}

			if(option < 1 || option > 7) {
				System.out.println("Sorry, but " + option + " is not an option, please try again");
				continue;
			}
			//----------------------------------------------------
			
			if(option == 1) {  
				Scanner scanOpt1 = new Scanner(System.in);
				try { scanFile= new Scanner(new File (scannerPath));} catch(Exception e) {}
				int facultyId = 0;
				String facultyName = "";
				String facultyRank = "";
				String facultyOfficeLocation = "";
				 ArrayList <String> facultyLabs = new ArrayList<String>(); 
				int lectures = 0;
				String userInput;
				boolean validId = false;
				boolean validLectures = false;
				boolean idFound = false;
				String[] inputCrns = null;
			
				System.out.println("Enter UCF id: ");
				// Check the user entered a valid ID
				//----------------------------------------------------------
				facultyId = getValidId(validId);
				//-----------------------------------------------------------
				idFound = idFound(members, facultyId);

				// No Id found. Request info to create an object of type faculty. If Id exist, do not add a new faculty to the ArrayList
				if(!idFound) {
					System.out.println("Enter name: ");
					facultyName = scanOpt1.nextLine();
					
					// Check the user entered a valid rank
					//---------------------------------------------------------------
					System.out.println("Enter rank: ");
					facultyRank = scanOpt1.nextLine();
					while(!facultyRank.equalsIgnoreCase("Professor") && 
						!facultyRank.equalsIgnoreCase("adjunct") && 
						!facultyRank.equalsIgnoreCase("associate professor") &&
						!facultyRank.equalsIgnoreCase("assistant professor")) 
					{
						System.out.println("Sorry, but " + facultyRank + " is not a valid rank.");
						System.out.println("Valid ranks are: professor, associate professor, assistant professor, or adjunct\nPlease try again"); 
						facultyRank = scanOpt1.nextLine();
					}
					//---------------------------------------------------------------
					
					System.out.println("Enter office location: ");
					facultyOfficeLocation = scanOpt1.nextLine();
					members.add(new Faculty(facultyId, facultyName, facultyRank, facultyOfficeLocation));
				}
				
				// Check the user entered a valid lecture number
				//---------------------------------------------------------------
				System.out.println("Enter how many lectures: ");
				while(!validLectures) {
					try {
						userInput = scanOpt1.nextLine();	
						lectures = Integer.parseInt(userInput);
						validLectures = true;
					} 
					catch(Exception e) 
					{
						System.out.println("Please enter a decimal number for the lectures");
					} 
				}
				//---------------------------------------------------------------
				
				// Check that the number of crns and number of lectures are the same
				//---------------------------------------------------------------
				System.out.println("Enter the crns of the lectures: ");		

				do {
					userInput = scanOpt1.nextLine();
					inputCrns = userInput.split(" ");
					if(inputCrns.length != lectures) {
						System.out.println("Sorry, but the number of crns provided does not match with the number of lectures provided.");
						System.out.println("Please enter " + lectures + " crns with a space in between");
						inputCrns[0] = "" ;
					}
				} while(inputCrns.length != lectures );
				//---------------------------------------------------------------
				
				//Check that the entered crns are not already assigned to another faculty inside the array list.
				//---------------------------------------------------------------
				for(UcfMember member : members) {  							// Check all faculty members of the array list
					if(member instanceof Faculty) {
						for(String checkCrn : member.getCrnOnly()) { 		// Check the crns of all faculty members of the array lisst
							for(String userCrn : inputCrns) {				// Check the all the user' inptut crn
								if(checkCrn != null && userCrn != null && checkCrn.equals(userCrn)) {		// Get all the crn of that faculty member and compare it with all user crns inputs 
									System.out.println("Crn " + userCrn + " has been assigned to another faculty. This crn will be not be assigned to this faculty member");
									for(int i = 0; i < inputCrns.length; i++) { //Since the user entered an already assigned crn, set that input as null
										if(inputCrns[i] == userCrn) {
											inputCrns[i] = null; 		
										}
									}
								}
							}
						}
					}
				}
				
				// Search the crns inside the text file and assign the crn to the faculty member
				//---------------------------------------------------------------
				if(!scanFile.hasNextLine()) {  //Each time it searches, the scan will be run out of lines, so create a new scanner.
					try { scanFile= new Scanner(new File (scannerPath));} catch(Exception e) {}
				}
					while(scanFile.hasNextLine()) {
						String classes = scanFile.nextLine();
						String [] infoOfClass = classes.split(",");
						UcfMember member = findMember(members, facultyId);
						for(int i = 0; i < inputCrns.length; i++) { 
							if(inputCrns[i] != null && infoOfClass[0].equalsIgnoreCase(inputCrns[i])) { // Find the same CRN in the text file as the user input
								if(infoOfClass.length > 2 && infoOfClass[4].equalsIgnoreCase("Online") || infoOfClass.length > 5 && infoOfClass[6].equalsIgnoreCase("NO")) { //If it is online or doesn't have a lab, add it to the faculty
									if(member != null) {
										member.getCrnOnly().add(infoOfClass[0]); // Get only the crn. Used to check that that crn has not been assigned to another faculty.
										member.getClasses().add(classes); // Assign the entire class to this faculty
										System.out.println("[" + infoOfClass[0] + "/" + infoOfClass[1] + "/"+ infoOfClass[2] + "]" + "Added!");
									}
								} 
								//Handling the case that a class contains a lab
								//-------------------------------------------------------------------------------
								else if(infoOfClass.length > 5 && infoOfClass[6].equalsIgnoreCase("YES")) {
									System.out.println("[" + infoOfClass[0] + "/" + infoOfClass[1] + "/"+ infoOfClass[2] + "]" + "has these labs:");
									int indexLab = 0;
									String addToTA = "";
									String [] crnOfLabs = new String[300];
									String [] storeLabsInfo = new String[300];
									String labs = "";
									int labCount = 0;
									boolean alreadyAssigned = false;
									while(scanFile.hasNextLine()) { 				//Get the CRN of the labs and store them in crnOfLabs. Display the labs.
										labs = scanFile.nextLine();
										if(labs.toLowerCase().contains("yes") || labs.toLowerCase().contains("no") || labs.toLowerCase().contains("online")) {
											break;
										}
										storeLabsInfo[indexLab] = labs;
										String [] infoOfLabs = labs.split(",");			
										System.out.println("\t"+labs);
										crnOfLabs[indexLab] = infoOfLabs[0];
										indexLab ++;
									}
									
									for(String crn : crnOfLabs) {
										if(crn != null) {
											int taId = 0;
											boolean validTAId = false;
											boolean foundTAId = false;
											boolean canBeTA = false;
											boolean taIsStudent = false;

											String taName = "";
											String taSupervisor = "";
											String degreeSeeking = "";
											while(!canBeTA) {			

												System.out.println("Enter the TA's id for " + crn);
												taId = getValidId(validTAId);
												foundTAId = idFound(members, taId);
												member = findMember(members, taId);
												//Check if TA's are students taking this lecture
												//----------------------------------------------------------------------
												if(member == null) {
													canBeTA = true; //No TA on list, meaning create a new one
													taIsStudent = false;
												}
												else if(member instanceof Student) {
														if(member.getClasses().isEmpty() == false) { // If the member has at least one class, then it is a student
															taIsStudent = true;	                    
															System.out.println("TA found as a student: " + member.getName()); 
															taName = member.getName();
															for(String lecture : member.getCrnOnly()) {
																if(lecture != null && lecture.equalsIgnoreCase(infoOfClass[0])) { //TA/Student is taking this lecture
																	System.out.println("Sorry, A student can’t be a TA for a lecture in which that student is taking.\n Please try again");
																	canBeTA = false;
																	break;
																}
																canBeTA = true;
															}
														}
														else {
															canBeTA = true;
														}
												}
												if(canBeTA == false) continue;
												//-------------------------------------------------------------------------
												
												// Convert the student into a TA. Add it to the arrayList
												//--------------------------------------------------------------------------
												if(taIsStudent) { 
													TA holdValue;
													member = findMember(members, taId);
													if(member instanceof TA && ((TA) member).getAdvisor() != null && ((TA) member).getExpectedDegree() != null) {
														taSupervisor =  ((TA) member).getAdvisor();
														degreeSeeking = ((TA) member).getExpectedDegree();
														holdValue =  new TA (((Student)member), taSupervisor, degreeSeeking );

													}
													else 
													{
														String[] taInfo = new String[2];
														taInfo = requestTAInfo(taSupervisor, degreeSeeking);
														holdValue =  new TA (((Student)member), taInfo[0], taInfo[1] );
													}
															members.remove(member);
															members.add(holdValue);
															member = findMember(members, taId);
															
												}
												//--------------------------------------------------------------------------
												
												 //Create a brand new TA and add it to the list
												//----------------------------------------------------------------------------
												if(!foundTAId) {
													System.out.println("Name of TA: ");
													taName =  scanOpt1.nextLine();
													String[] taInfo = new String[2];
													taInfo = requestTAInfo(taSupervisor, degreeSeeking);
													members.add(new TA(taId, taName,  taInfo[0], taInfo[1]));
													member = findMember(members, taId);
												}
												//----------------------------------------------------------------------------
														
														for(int index = 0 ; index < storeLabsInfo.length; index++) {
															if(storeLabsInfo[index] != null) {
																
																facultyLabs.add(storeLabsInfo[index]);
																if(index == labCount) {
																	addToTA = storeLabsInfo[index];
																}
															}
														}
														labCount++;
														
														try {
															// Get the TA and add the labs to its TA labs
															//-------------------------------------------------------------------------
															((TA) member).getTaLab().add(addToTA);
															if(((TA) member).getTaCrnLecture().contains(infoOfClass[0]) == false)
																((TA) member).getTaCrnLecture().add(infoOfClass[0]);					
															//-------------------------------------------------------------------------
														} catch(Exception e) {
															System.out.println("Something went wrong. Probably the user entered a wrong crn to enroll a student ");
														}
														
											}
										}
									}	
									member = findMember(members, facultyId);
									member.getCrnOnly().add(infoOfClass[0]); 
									member.getClasses().add(classes); // Assign the entire class to the TA
									for(String labstoAdd : facultyLabs) {
										if(member.getLabs().contains(labstoAdd)) continue;
										member.getLabs().add(labstoAdd);
									}
									try {
										lectureAndLabs.add(infoOfClass[0] + "*" + facultyLabs.get(lectureAndLabsIndex) + "*" +  facultyLabs.get(lectureAndLabsIndex+1)+ "*" +  facultyLabs.get(lectureAndLabsIndex+2) );
										lectureAndLabsIndex += 3;	
									} catch(Exception e) {
										System.out.println("There are more than or less than 3 labs for this lecture.");
									}
									System.out.println("[" + infoOfClass[0] + "/" + infoOfClass[1] + "/"+ infoOfClass[2] + "]" + "Added!");
								}
							}
						}
					}
				}// End of option 1 ---------------------------------------------------
				else if(option == 2) {
					try { scanFile= new Scanner(new File (scannerPath));} catch(Exception e) {}
					Scanner scanOpt2 = new Scanner(System.in);
					int studentId = 0;
					String studentName = "";
					String studentType = "";
					String studentLectureCrn = "";
					boolean validId = false;
					boolean idFound = false;
					UcfMember member;
					System.out.println("Enter UCF id: ");

					studentId = getValidId(validId);

					idFound = idFound(members, studentId);
					if(!idFound) {
						System.out.println("Student not found in the system. Let's add it to the system. ");
						System.out.println("Enter name: ");
						studentName = scanOpt2.nextLine();
						System.out.println("Enter the type of student, either graduate or undergraduate: ");
						studentType = validInput("undergraduate", "graduate");
						members.add(new Student(studentId, studentName, studentType));
					}
					else {
						member = findMember(members, studentId);
						System.out.println("Record found/Name: " + member.getName());
					}
					
					member = findMember(members, studentId);
					System.out.println("Which lecture to enroll ["+ member.getName() + "] in?");
					studentLectureCrn = scanOpt2.nextLine();
					
					// Cheking that the student is not a TA for this lecture
					//------------------------------------------------------------
					if(member instanceof TA) {
						for(String crnLecture :((TA) member).getTaCrnLecture()) {
							while(crnLecture.equals(studentLectureCrn)) { //This TA student have this lecture as TA
								System.out.println("Sorry, but cannot enroll this student in this lecture because the student is a TA in one of its labs.");
								System.out.println("Please insert another crn lecture");
								studentLectureCrn = scanOpt2.nextLine();
							}
						}
					}
					//------------------------------------------------------------

					if(!scanFile.hasNextLine()) {
						try { scanFile= new Scanner(new File (scannerPath));} catch(Exception e) {}
					}
						while(scanFile.hasNextLine()) {
							String classes = scanFile.nextLine();
							String [] infoOfClass = classes.split(",");
							if(studentLectureCrn != null && infoOfClass[0].equalsIgnoreCase(studentLectureCrn)) { // Find the same CRN in the text file as the user input
								if(infoOfClass.length > 5 && infoOfClass[6].equalsIgnoreCase("YES")) {
									System.out.println("[" + infoOfClass[0] + "/" + infoOfClass[1] + "/"+ infoOfClass[2] + "]" + "has these labs:");
									int indexLab = 0;
									String [] crnOfLabs = new String[300];
									String [] storeLabsInfo = new String[300];
									String labs = "";
									while(scanFile.hasNextLine()) { 				//Get the CRN of the labs and store them in crnOfLabs. Display the labs.
										labs = scanFile.nextLine();
										storeLabsInfo[indexLab] = labs;
										String [] infoOfLabs = labs.split(",");			
										if(labs.toLowerCase().contains("yes") || labs.toLowerCase().contains("no") || labs.toLowerCase().contains("online")) {
											break;
										}
										System.out.println("\t"+labs);
										crnOfLabs[indexLab] = infoOfLabs[0];
										indexLab ++;
									}
									// Get a random lab 
									//------------------------------------------------
									int notNullCount = 0;
									for(String count : crnOfLabs) {
										if(count == null) {
											notNullCount++;
										}
									}
									int random_int = (int)Math.floor(Math.random() * (crnOfLabs.length - notNullCount));
									member.getLabs().add(crnOfLabs[random_int]);
									System.out.println("[" + member.getName() + "]  is added to lab : " + crnOfLabs[random_int]);
									//------------------------------------------------
								}
								member.getCrnOnly().add(infoOfClass[0]); 
								member.getClasses().add(classes); 
								System.out.println("Student enrolled!");
							}
				}
				} //End of option 2 ------------------------------------------------------
				else if(option == 3) {
					UcfMember member = startPrintOption(members, "Faculty");
					if(member != null) {					
						System.out.println(member.getName() +" with UCFID " + member.getId() + " is teaching the following lectures: ");
						printSchedule(member);
					}
				}//End of option 3 ------------------------------------------------------
				else if(option == 4) {
					UcfMember member = startPrintOption(members, "TA");
					if(member != null) {
						System.out.println(member.getName() +" with UCFID " + member.getId() + " is a teacher assitent in the following lectures: ");
						for(String crnLecture : ((TA)member).getTaCrnLecture()) {
							System.out.println("\t["+crnLecture +"]");
						}
						System.out.println(member.getName() + " works in the following labs:");
						for(String taLab : ((TA)member).getTaLab()) {
							System.out.println("\t["+taLab +"]");
						}
					}
				}//End of option 4 ------------------------------------------------------
				else if(option ==5) {
					UcfMember member = startPrintOption(members, "Student");
					int index = 0;
					if(member != null) {
						System.out.println("Record Found:\n\t" + member.getName() + "\n\tEnrolled in the following lectures: " );
						printSchedule(member);
					}
				}//End of option 5 ------------------------------------------------------
			
				else if(option == 6) {
			        String [] lectureInfo = null;
					Scanner scanOpt6 = new Scanner(System.in); 
					String delete = null;
					System.out.println("Enter the crn of the lecture to delete");
					String deleteLecture = scanOpt6.nextLine();
					// Deleting the lectures and labs from every member
					//--------------------------------------------------------------------------------
					for(UcfMember member : members) {
						deleteLectureFromArray(member.getCrnOnly(), deleteLecture);
						deleteLectureFromArray(member.getClasses(), deleteLecture);
						if(member instanceof TA) {
							deleteLectureFromArray(((TA)member).getTaCrnLecture(), deleteLecture);
						}
						
						for(String labs : lectureAndLabs) {
							if(labs.contains(deleteLecture)) {
								deleteLabFromArray(member.getLabs(), labs, 1);
								deleteLabFromArray(member.getLabs(), labs, 2);
								deleteLabFromArray(member.getLabs(), labs, 3);

								
								if(member instanceof TA) {
									deleteLectureFromArray(((TA)member).getTaCrnLecture(), deleteLecture);
									deleteLabFromArray(((TA)member).getTaLab(), labs, 1);
									deleteLabFromArray(((TA)member).getTaLab(), labs, 2);
									deleteLabFromArray(((TA)member).getTaLab(), labs, 3);
								}
							}
						}
						
					}
					//--------------------------------------------------------------------------------
					// Deliting the lecture and labs from the file
					//--------------------------------------------------------------------------------
			        String lecture = null;
			        String line = null;
			        String lab1 = null;
			        String lab2 = null;
			        String lab3 = null;
			        boolean hasLabs = false;
			        
			        if(!scanFile.hasNextLine()) {
						try { scanFile= new Scanner(new File (scannerPath));} catch(Exception e) {}
					}
						while(scanFile.hasNextLine()) {
							lecture = scanFile.nextLine();
							// System.out.println(lecture);
							if(lecture.contains(deleteLecture)) {
								lectureInfo = lecture.split(",");
								if(lectureInfo.length >5 && lectureInfo[6].equalsIgnoreCase("yes")) {
									lab1 = scanFile.nextLine();
									lab2 = scanFile.nextLine();
									lab3 = scanFile.nextLine();
									hasLabs = true;
								}
								try { scanFile= new Scanner(new File (scannerPath));} catch(Exception e) {}
								break;
							}
						}
			        deleteFromFile(scannerPath, lecture);

			        if(hasLabs) {
			        	  deleteFromFile(scannerPath, lab1);
			        	  deleteFromFile(scannerPath, lab2);
			        	  deleteFromFile(scannerPath, lab3);
			        }
					//--------------------------------------------------------------------------------
			        try {
						System.out.println("[" + lectureInfo[0] + "/" + lectureInfo[1] + "/"+ lectureInfo[2] + "]" + "Deleted");
						fileUpdated = true;
			        }
			        catch(Exception e){
			        	System.out.println("Deleted.");
			        }
				}// End of option 6-------------------
		}
		if(fileUpdated) {
			System.out.println("You have made a deletion of at least one lecture. Would you like to print the copy of lec.txt? Enter y/Y for Yes or n/N for No:");
			String input = scanInput.next();
			while(input.equalsIgnoreCase("y") == false && input.equalsIgnoreCase("n") == false ) {
				System.out.print("Is that a yes or no? Enter y/Y for Yes or n/N for No:");
				input = scanInput.next();
			}
			if(input.equalsIgnoreCase("y") ) {
				System.out.println("Sure, here is a copy of the file:\n");
				try (BufferedReader br = new BufferedReader(new FileReader(scannerPath))) {
					   String line;
					   while ((line = br.readLine()) != null) {
						   if(line.equals("")) continue;
					       System.out.println(line);
					   }
					} catch(Exception e) {
						System.out.println("File not found");
					}
			}
		}
		System.out.println("\nBye!");
		
	}
}
abstract class UcfMember{
	//Variables
	private String name;
	private int id;
	private ArrayList <String> crnOnly = new ArrayList<String>(); //Crns of the lectures only. Not for TA's lectures labs
	private ArrayList <String> classes = new ArrayList<String>();// All the information about their class
	private ArrayList <String> labs = new ArrayList<String>(); // Required information about the lectures
	
	//Getters and setters
	public ArrayList<String> getClasses() {
		return classes;
	}
	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}
	public ArrayList<String> getLabs() {
		return labs;
	}
	public void setLabs(ArrayList<String> labs) {
		this.labs = labs;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public ArrayList<String> getCrnOnly() {
		return crnOnly;
	}
	public void setCrnOnly(ArrayList<String> crnOnly) {
		this.crnOnly = crnOnly;
	}

}
class Faculty extends UcfMember{
	//Variables 
	private String rank;
	private String location;
	
	// Getters and setters
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	//Constructors
	Faculty(int id, String name, String rank, String location){
		setName(name);
		setId(id);
		this.location = location;
		this.rank = rank;
	}
}

class Student extends UcfMember{
	//Variables
	private String studentType;
	
	//Getters and setters	
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	//Constructors
	Student(int id, String name, String studentType){
		setName(name);
		setId(id);
		this.studentType = studentType;
	}
	Student(){};
}
class TA extends Student{
	//Variables
	private String advisor;
	private String expectedDegree;
	private ArrayList <String> taLab = new ArrayList<String>();        // The labs that the TA is working in
	private ArrayList <String> taCrnLecture = new ArrayList<String>(); //Crn of lectures in which the TA is doing the labs

	// Getters and setters
	public ArrayList<String> getTaCrnLecture() {
		return taCrnLecture;
	}
	public void setTaCrnLecture(ArrayList<String> taLecture) {
		this.taCrnLecture = taLecture;
	}
	public ArrayList<String> getTaLab() {
		return taLab;
	}
	public void setTaLab(ArrayList<String> taLab) {
		this.taLab = taLab;
	}
	public String getAdvisor() {
		return advisor;
	}
	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}
	public String getExpectedDegree() {
		return expectedDegree;
	}
	public void setExpectedDegree(String expectedDegree) {
		this.expectedDegree = expectedDegree;
	}
	
	//Constructor
	TA(int id, String name, String advisor, String expectedDegree){
		setName(name);
		setId(id);
		this.advisor = advisor;
		this.expectedDegree = expectedDegree;
	}
	TA(Student student, String advisor, String expectedDegree){
		super(student.getId(), student.getName(), student.getStudentType());
		setCrnOnly(student.getCrnOnly());
		setClasses(student.getClasses()); 
		setLabs(student.getLabs());  
		this.advisor = advisor;
		this.expectedDegree = expectedDegree;
	}
}
class IdException extends Exception{
	private int id;
	public IdException(int id) {
		this.id = id;
	}
	@Override
	public String getMessage() {
		return ">>>>>>>>>>>Sorry incorrect format. (Ids are 7 digits)";
	}
}
